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

private const val STREAM_THROTTLE_MS = 40L

data class ChatState(
    val turns: List<ChatTurn> = emptyList(),
    val liveReasoning: String = "",
    val liveText: String = "",
    val liveTools: List<ToolCallRecord> = emptyList(),
    val loading: Boolean = false,
    val errorText: String = "",
    val conversationId: String = "",
)

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

    private fun clearLive() = state.copy(
        liveReasoning = "",
        liveText = "",
        liveTools = emptyList(),
        loading = false,
    )

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
        val current = state
        val leftText = current.liveText
        if (leftText.isNotBlank()) {
            scope.launch {
                repository.appendMessage(current.conversationId, MessageRole.Assistant, leftText)
            }
        }
        publish(
            current.copy(
                turns = if (leftText.isBlank()) {
                    current.turns
                } else {
                    current.turns + ChatTurn(
                        role = "assistant",
                        content = leftText,
                        reasoning = current.liveReasoning,
                        toolCalls = current.liveTools,
                    )
                },
                liveReasoning = "",
                liveText = "",
                liveTools = emptyList(),
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
                liveReasoning = "",
                liveText = "",
                liveTools = emptyList(),
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
                liveReasoning = "",
                liveText = "",
                liveTools = emptyList(),
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
                    val text = StringBuilder()
                    val reasoning = StringBuilder()
                    val toolAcc = LinkedHashMap<Int, ToolAccumulator>()
                    var lastTextAt = 0L
                    var lastReasonAt = 0L
                    var publishedText = 0

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
                            is ChatChunk.Delta -> {
                                text.append(chunk.text)
                                val now = System.currentTimeMillis()
                                if (now - lastTextAt >= STREAM_THROTTLE_MS || text.length - publishedText >= 48) {
                                    lastTextAt = now
                                    publishedText = text.length
                                    publish(
                                        state.copy(
                                            liveText = text.toString(),
                                            liveReasoning = reasoning.toString(),
                                            loading = false,
                                        ),
                                    )
                                }
                            }

                            is ChatChunk.ReasoningDelta -> {
                                reasoning.append(chunk.text)
                                val now = System.currentTimeMillis()
                                if (now - lastReasonAt >= STREAM_THROTTLE_MS) {
                                    lastReasonAt = now
                                    publish(
                                        state.copy(
                                            liveReasoning = reasoning.toString(),
                                            loading = false,
                                        ),
                                    )
                                }
                            }

                            is ChatChunk.ToolCallDelta -> {
                                val acc = toolAcc.getOrPut(chunk.index) { ToolAccumulator() }
                                if (chunk.id.isNotBlank()) acc.id = chunk.id
                                if (chunk.name.isNotBlank()) acc.name = chunk.name
                                acc.arguments.append(chunk.arguments)
                            }

                            else -> Unit
                        }
                    }

                    val finalText = text.toString()
                    val finalReasoning = reasoning.toString()
                    publish(
                        state.copy(
                            liveText = finalText,
                            liveReasoning = finalReasoning,
                            loading = false,
                        ),
                    )

                    val pendingCalls = toolAcc.values.filter { it.name.isNotBlank() && it.id.isNotBlank() }

                    if (!settings.toolsEnabled || pendingCalls.isEmpty()) {
                        val turn = ChatTurn(
                            role = "assistant",
                            content = finalText,
                            reasoning = finalReasoning,
                        )
                        if (finalText.isNotBlank()) {
                            repository.appendMessage(conversationId, MessageRole.Assistant, finalText)
                        }
                        publish(
                            clearLive().copy(
                                turns = state.turns + turn,
                            ),
                        )
                        break
                    }

                    rounds++
                    if (rounds > settings.maxToolRounds) {
                        publish(clearLive().copy(errorText = "工具调用超过 ${settings.maxToolRounds} 轮"))
                        break
                    }

                    history.add(
                        ChatTurn(
                            role = "assistant",
                            content = finalText,
                            reasoning = finalReasoning,
                        ),
                    )

                    val executed = mutableListOf<ToolCallRecord>()
                    val runningList = pendingCalls.map {
                        ToolCallRecord(name = it.name, arguments = it.arguments.toString(), running = true)
                    }
                    publish(
                        state.copy(
                            liveReasoning = finalReasoning,
                            liveText = "",
                            liveTools = runningList,
                            loading = false,
                        ),
                    )

                    pendingCalls.forEachIndexed { index, call ->
                        val argsText = call.arguments.toString()
                        val output = NovaTools.execute(call.name, argsText)
                        executed.add(
                            ToolCallRecord(
                                name = call.name,
                                arguments = argsText,
                                result = output,
                                running = false,
                            ),
                        )
                        val updated = runningList.toMutableList()
                        if (index < updated.size) {
                            updated[index] = executed.last()
                        }
                        publish(state.copy(liveTools = updated.toList()))
                        history.add(
                            ChatTurn(
                                role = "tool",
                                content = output,
                                name = call.name,
                                toolCallId = call.id,
                            ),
                        )
                    }

                    state = state.copy(
                        liveReasoning = finalReasoning,
                        liveText = "",
                        liveTools = executed,
                    )
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
                publish(
                    clearLive().copy(errorText = readable(e.error)),
                )
            } catch (e: Exception) {
                publish(clearLive().copy(errorText = e.message ?: "未知错误"))
            }
        }
    }

    private class ToolAccumulator {
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
