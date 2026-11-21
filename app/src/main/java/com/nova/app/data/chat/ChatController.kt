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

data class ChatState(
    val turns: List<ChatTurn> = emptyList(),
    val streamingText: String = "",
    val loading: Boolean = false,
    val errorText: String = "",
    val conversationId: String = "",
    val toolLabel: String = "",
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
        if (current.streamingText.isNotBlank()) {
            val text = current.streamingText
            val conversationId = current.conversationId
            scope.launch { repository.appendMessage(conversationId, MessageRole.Assistant, text) }
            publish(
                current.copy(
                    turns = current.turns + ChatTurn("assistant", text),
                    streamingText = "",
                    loading = false,
                    toolLabel = "",
                ),
            )
        } else {
            publish(current.copy(loading = false, streamingText = "", toolLabel = ""))
        }
    }

    fun send(text: String) {
        val provider = activeProvider() ?: run {
            publish(state.copy(errorText = "no provider", loading = false))
            return
        }
        val model = activeModel() ?: run {
            publish(state.copy(errorText = "no model", loading = false))
            return
        }

        val current = state
        val nextTurns = current.turns + ChatTurn("user", text)
        publish(
            current.copy(
                turns = nextTurns,
                streamingText = "",
                loading = true,
                errorText = "",
                toolLabel = "",
            ),
        )

        scope.launch {
            var conversationId = current.conversationId
            if (conversationId.isBlank()) {
                conversationId = repository.createConversation(fallbackTitle(text))
                publish(state.copy(conversationId = conversationId))
            }
            repository.appendMessage(conversationId, MessageRole.User, text)

            val isFirstUserMessage = current.turns.none { it.role == "user" }
            runChat(conversationId, provider, model, nextTurns, isFirstUserMessage)
        }
    }

    fun retry() {
        val current = state
        val lastUserIndex = current.turns.indexOfLast { it.role == "user" }
        if (lastUserIndex < 0) return
        val trimmed = current.turns.take(lastUserIndex + 1)
        val provider = activeProvider() ?: return
        val model = activeModel() ?: return
        publish(
            current.copy(
                turns = trimmed,
                errorText = "",
                streamingText = "",
                loading = true,
                toolLabel = "",
            ),
        )
        scope.launch {
            runChat(current.conversationId, provider, model, trimmed, false)
        }
    }

    private suspend fun runChat(
        conversationId: String,
        provider: Provider,
        model: String,
        baseTurns: List<ChatTurn>,
        generateTitle: Boolean,
    ) {
        val settings = GenerationStore.load(context)
        val systemPrompt = AssistantStore.buildSystemPrompt(AssistantStore.load(context))

        job?.cancel()
        job = scope.launch {
            val history = baseTurns.toMutableList()
            var rounds = 0
            try {
                while (true) {
                    val accumulated = StringBuilder()
                    val toolCalls = LinkedHashMap<Int, ToolAccumulator>()

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
                                accumulated.append(chunk.text)
                                publish(
                                    state.copy(
                                        streamingText = accumulated.toString(),
                                        loading = false,
                                        toolLabel = "",
                                    ),
                                )
                            }

                            is ChatChunk.ToolCallDelta -> {
                                val acc = toolCalls.getOrPut(chunk.index) { ToolAccumulator() }
                                if (chunk.id.isNotBlank()) acc.id = chunk.id
                                if (chunk.name.isNotBlank()) acc.name = chunk.name
                                acc.arguments.append(chunk.arguments)
                                if (settings.toolsEnabled) {
                                    val label = acc.name.ifBlank { "tool" }
                                    publish(state.copy(toolLabel = label))
                                }
                            }

                            else -> Unit
                        }
                    }

                    val text = accumulated.toString()
                    val readyCalls = toolCalls.values.filter { it.name.isNotBlank() }

                    if (readyCalls.isEmpty() || !settings.toolsEnabled) {
                        if (text.isNotBlank()) {
                            repository.appendMessage(conversationId, MessageRole.Assistant, text)
                        }
                        publish(
                            state.copy(
                                turns = state.turns + ChatTurn("assistant", text),
                                streamingText = "",
                                loading = false,
                                toolLabel = "",
                            ),
                        )
                        break
                    }

                    rounds++
                    history.add(ChatTurn("assistant", text))

                    if (rounds > settings.maxToolRounds) {
                        publish(
                            state.copy(
                                streamingText = "",
                                loading = false,
                                toolLabel = "",
                                errorText = "tool rounds exceeded",
                            ),
                        )
                        break
                    }

                    readyCalls.forEach { call ->
                        publish(state.copy(toolLabel = call.name))
                        val output = NovaTools.execute(call.name, call.arguments.toString())
                        history.add(
                            ChatTurn(
                                role = "tool",
                                content = output,
                                name = call.name,
                                toolCallId = call.id,
                            ),
                        )
                    }
                    publish(state.copy(streamingText = "", toolLabel = ""))
                }

                if (generateTitle && settings.autoTitle) {
                    val title = TitleGenerator.generate(context, provider, model, history)
                        ?: TitleGenerator.fallbackTitle(history)
                    if (title.isNotBlank()) {
                        repository.rename(conversationId, title)
                        onTitleChanged(conversationId, title)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: ChatException) {
                val partial = state.streamingText
                publish(
                    state.copy(
                        turns = if (partial.isBlank()) {
                            state.turns
                        } else {
                            state.turns + ChatTurn("assistant", partial)
                        },
                        streamingText = "",
                        loading = false,
                        toolLabel = "",
                        errorText = readable(e.error),
                    ),
                )
            } catch (e: Exception) {
                publish(
                    state.copy(
                        streamingText = "",
                        loading = false,
                        toolLabel = "",
                        errorText = e.message ?: "unknown",
                    ),
                )
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

    private fun fallbackTitle(text: String): String =
        text.trim().lineSequence().firstOrNull().orEmpty().take(10).ifBlank { "新对话" }

    private fun activeProvider(): Provider? {
        val providers = ProviderStore.load(context)
        val activeId = ProviderStore.activeId(context)
        return providers.firstOrNull { it.id == activeId } ?: providers.firstOrNull()
    }

    private fun activeModel(): String? {
        val provider = activeProvider() ?: return null
        val selected = ProviderStore.activeModel(context, provider.id)
        if (selected.isNotBlank() && provider.models.any { it.id == selected }) return selected
        return provider.models.firstOrNull()?.id
    }
}
