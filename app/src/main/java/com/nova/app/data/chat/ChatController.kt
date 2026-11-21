package com.nova.app.data.chat

import android.content.Context
import com.nova.app.data.AssistantStore
import com.nova.app.data.ConversationRepository
import com.nova.app.data.MessageRole
import com.nova.app.data.provider.GenerationStore
import com.nova.app.data.provider.Provider
import com.nova.app.data.provider.ProviderStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val PUBLISH_INTERVAL_MS = 60L

data class ChatState(
    val turns: List<ChatTurn> = emptyList(),
    val liveParts: List<ChatPart> = emptyList(),
    val loading: Boolean = false,
    val errorText: String = "",
    val conversationId: String = "",
) {
    val visibleParts: List<ChatPart>
        get() = liveParts.visibleParts()

    val hasLive: Boolean
        get() = loading || errorText.isNotEmpty() || visibleParts.isNotEmpty()
}

class ChatController(
    private val context: Context,
    private val repository: ConversationRepository,
    private val scope: CoroutineScope,
    private val onState: (ChatState) -> Unit,
    private val onTitleChanged: (String, String) -> Unit = { _, _ -> },
) {

    private var job: Job? = null
    private var state = ChatState()

    private fun publish(next: ChatState) {
        state = next
        onState(next)
    }

    fun reset() {
        job?.cancel()
        job = null
        publish(ChatState())
    }

    fun load(conversationId: String) {
        job?.cancel()
        job = null
        publish(ChatState(conversationId = conversationId, loading = true))
        scope.launch {
            val messages = runCatching { repository.messagesOf(conversationId) }.getOrDefault(emptyList())
            publish(
                ChatState(
                    conversationId = conversationId,
                    turns = messages.map { ChatTurn(it.role.value, it.content) },
                ),
            )
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        val parts = state.liveParts
        val text = parts.textContent()
        if (text.isNotBlank()) {
            scope.launch {
                repository.appendMessage(state.conversationId, MessageRole.Assistant, text)
            }
        }
        publish(
            state.copy(
                turns = if (text.isBlank()) {
                    state.turns
                } else {
                    state.turns + ChatTurn(role = "assistant", content = text, parts = parts)
                },
                liveParts = emptyList(),
                loading = false,
            ),
        )
    }

    fun send(text: String) {
        val provider = activeProvider()
        if (provider == null) {
            publish(state.copy(errorText = "未配置提供商", loading = false))
            return
        }
        val model = activeModel(provider)
        if (model == null) {
            publish(state.copy(errorText = "未选择模型", loading = false))
            return
        }

        val current = state
        val nextTurns = current.turns + ChatTurn("user", text)
        publish(
            current.copy(
                turns = nextTurns,
                liveParts = emptyList(),
                loading = true,
                errorText = "",
            ),
        )

        scope.launch {
            var conversationId = current.conversationId
            if (conversationId.isBlank()) {
                conversationId = repository.createConversation(text.trim().take(10).ifBlank { "新对话" })
                publish(state.copy(conversationId = conversationId))
            }
            repository.appendMessage(conversationId, MessageRole.User, text)
            runChat(conversationId, provider, model, nextTurns)
        }
    }

    fun retry() {
        val current = state
        val lastUserIndex = current.turns.indexOfLast { it.role == "user" }
        if (lastUserIndex < 0) return
        val trimmed = current.turns.take(lastUserIndex + 1)
        val provider = activeProvider() ?: return
        val model = activeModel(provider) ?: return
        publish(
            current.copy(
                turns = trimmed,
                liveParts = emptyList(),
                errorText = "",
                loading = true,
            ),
        )
        scope.launch { runChat(current.conversationId, provider, model, trimmed) }
    }

    private suspend fun runChat(
        conversationId: String,
        provider: Provider,
        model: String,
        baseTurns: List<ChatTurn>,
    ) {
        val settings = GenerationStore.load(context)
        val systemPrompt = AssistantStore.buildSystemPrompt(AssistantStore.load(context))

        job?.cancel()
        job = scope.launch {
            val history = baseTurns.toMutableList()
            var rounds = 0
            try {
                while (true) {
                    var parts: List<ChatPart> = emptyList()
                    val handler = StreamPartHandler()
                    val toolRequests = LinkedHashMap<Int, ToolRequest>()
                    var lastPublish = 0L

                    fun flush(force: Boolean) {
                        val now = System.currentTimeMillis()
                        if (!force && now - lastPublish < PUBLISH_INTERVAL_MS) return
                        lastPublish = now
                        publish(state.copy(liveParts = parts, loading = false))
                    }

                    ChatClient.send(
                        ChatRequest(
                            provider = provider,
                            model = model,
                            messages = history,
                            systemPrompt = systemPrompt,
                            reasoning = settings.reasoning.key.ifBlank { null },
                            tools = if (settings.toolsEnabled) NovaTools.definitions else emptyList(),
                            stream = settings.stream,
                        ),
                    ).collect { chunk ->
                        when (chunk) {
                            is ChatChunk.ToolCallDelta -> {
                                val req = toolRequests.getOrPut(chunk.index) { ToolRequest() }
                                if (chunk.id.isNotBlank()) req.id = chunk.id
                                if (chunk.name.isNotBlank()) req.name = chunk.name
                                req.arguments.append(chunk.arguments)
                            }

                            else -> {
                                parts = handler.handle(parts, chunk)
                                flush(false)
                            }
                        }
                    }

                    publish(state.copy(liveParts = parts, loading = false))

                    val pending = toolRequests.values.filter {
                        it.name.isNotBlank() && it.id.isNotBlank()
                    }

                    if (!settings.toolsEnabled || pending.isEmpty()) {
                        val text = parts.textContent()
                        val turn = ChatTurn(role = "assistant", content = text, parts = parts)
                        if (text.isNotBlank()) {
                            repository.appendMessage(conversationId, MessageRole.Assistant, text)
                        }
                        publish(
                            state.copy(
                                turns = state.turns + turn,
                                liveParts = emptyList(),
                                loading = false,
                            ),
                        )
                        break
                    }

                    rounds++
                    if (rounds > settings.maxToolRounds) {
                        publish(
                            state.copy(
                                errorText = "工具调用超过 ${settings.maxToolRounds} 轮",
                                liveParts = emptyList(),
                                loading = false,
                            ),
                        )
                        break
                    }

                    history.add(
                        ChatTurn(
                            role = "assistant",
                            content = parts.textContent(),
                            parts = parts,
                        ),
                    )

                    pending.forEach { req ->
                        parts = parts + ChatPart.Tool(
                            toolCallId = req.id,
                            name = req.name,
                            arguments = req.arguments.toString(),
                        )
                    }
                    publish(state.copy(liveParts = parts, loading = false))

                    pending.forEach { req ->
                        val args = req.arguments.toString()
                        val output = NovaTools.execute(req.name, args)
                        parts = handler.markToolExecuted(parts, req.id, output)
                        publish(state.copy(liveParts = parts))
                        history.add(
                            ChatTurn(
                                role = "tool",
                                content = output,
                                name = req.name,
                                toolCallId = req.id,
                            ),
                        )
                    }
                }

                if (settings.autoTitle && history.count { it.role == "user" } >= 1) {
                    val title = runCatching {
                        TitleGenerator.generate(context, provider, model, history)
                    }.getOrNull() ?: TitleGenerator.fallbackTitle(history)
                    if (title.isNotBlank()) {
                        repository.rename(conversationId, title)
                        onTitleChanged(conversationId, title)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: ChatException) {
                publish(state.copy(liveParts = emptyList(), loading = false, errorText = readable(e.error)))
            } catch (e: Exception) {
                publish(state.copy(liveParts = emptyList(), loading = false, errorText = e.message ?: "未知错误"))
            }
        }
    }

    private class ToolRequest {
        var id: String = ""
        var name: String = ""
        val arguments = StringBuilder()
    }

    private fun readable(error: ChatError): String = when (error) {
        is ChatError.Http -> "HTTP ${error.code} · ${error.detail}"
        is ChatError.Network -> error.message
        is ChatError.InvalidResponse -> error.message
        is ChatError.Cancelled -> error.message
    }

    private fun activeProvider(): Provider? {
        val providers = ProviderStore.load(context)
        val activeId = ProviderStore.activeId(context)
        return providers.firstOrNull { it.id == activeId } ?: providers.firstOrNull()
    }

    private fun activeModel(provider: Provider): String? {
        val selected = ProviderStore.activeModel(context, provider.id)
        if (selected.isNotBlank() && provider.models.any { it.id == selected }) return selected
        return provider.models.firstOrNull()?.id
    }
}
