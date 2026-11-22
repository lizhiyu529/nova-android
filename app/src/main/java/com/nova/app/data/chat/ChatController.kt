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

private const val PUBLISH_INTERVAL_MS = 80L

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
                turns = if (text.isBlank() && parts.isEmpty()) {
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
            runAgent(conversationId, provider, model, nextTurns)
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
        scope.launch { runAgent(current.conversationId, provider, model, trimmed) }
    }

    private suspend fun runAgent(
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
            var carriedParts: List<ChatPart> = emptyList()

            try {
                while (true) {
                    val rawParts = streamOnce(
                        provider = provider,
                        model = model,
                        history = history,
                        systemPrompt = systemPrompt,
                        settings = settings,
                        baseParts = carriedParts,
                    )
                    val parts = ChatTurn(role = "assistant", parts = rawParts)
                        .finishReasoning()
                        .parts
                    carriedParts = parts

                    val pendingTools = parts.filterIsInstance<ChatPart.Tool>()
                        .filter { !it.isExecuted && it.toolName.isNotBlank() }

                    if (pendingTools.isEmpty()) {
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

                    var current = parts
                    pendingTools.forEach { tool ->
                        publish(state.copy(liveParts = current))
                        val output = NovaTools.execute(tool.toolName, tool.input)
                        current = current.map { part ->
                            if (part is ChatPart.Tool && part.toolCallId == tool.toolCallId) {
                                part.copy(output = output, isExecuted = true)
                            } else {
                                part
                            }
                        }
                        publish(state.copy(liveParts = current))
                    }

                    history.add(
                        ChatTurn(
                            role = "assistant",
                            content = current.textContent(),
                            parts = current,
                            toolCalls = pendingTools.map {
                                ToolCallRef(
                                    id = it.toolCallId,
                                    name = it.toolName,
                                    arguments = it.input,
                                )
                            },
                        ),
                    )

                    pendingTools.forEach { tool ->
                        val executed = current.filterIsInstance<ChatPart.Tool>()
                            .firstOrNull { it.toolCallId == tool.toolCallId }
                        history.add(
                            ChatTurn(
                                role = "tool",
                                content = executed?.output.orEmpty(),
                                name = tool.toolName,
                                toolCallId = tool.toolCallId,
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

    private suspend fun streamOnce(
        provider: Provider,
        model: String,
        history: List<ChatTurn>,
        systemPrompt: String,
        settings: com.nova.app.data.provider.GenerationSettings,
        baseParts: List<ChatPart> = emptyList(),
    ): List<ChatPart> {
        val handler = StreamPartHandler()
        var parts: List<ChatPart> = baseParts
        var lastPublish = 0L
        var loadingCleared = false

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
            parts = handler.handle(parts, chunk)
            val now = System.currentTimeMillis()
            if (!loadingCleared || now - lastPublish >= PUBLISH_INTERVAL_MS) {
                lastPublish = now
                loadingCleared = true
                publish(state.copy(liveParts = parts, loading = false))
            }
        }

        publish(state.copy(liveParts = parts, loading = false))
        return parts
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
