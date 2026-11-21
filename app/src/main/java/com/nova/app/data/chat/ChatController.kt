package com.nova.app.data.chat

import android.content.Context
import com.nova.app.data.AssistantProfile
import com.nova.app.data.AssistantStore
import com.nova.app.data.ConversationRepository
import com.nova.app.data.MessageRole
import com.nova.app.data.provider.Provider
import com.nova.app.data.provider.ProviderStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class ChatState(
    val turns: List<ChatTurn> = emptyList(),
    val streamingText: String = "",
    val loading: Boolean = false,
    val errorText: String = "",
    val conversationId: String = "",
)

class ChatController(
    private val context: Context,
    private val repository: ConversationRepository,
    private val scope: CoroutineScope,
    private val onState: (ChatState) -> Unit,
) {

    private var job: Job? = null
    private var state = ChatState()

    private fun publish(next: ChatState) {
        state = next
        onState(next)
    }

    fun snapshot(): ChatState = state

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
            runCatching { repository.messagesOf(conversationId) }
                .onSuccess { messages ->
                    publish(
                        ChatState(
                            conversationId = conversationId,
                            turns = messages.map { ChatTurn(it.role.value, it.content) },
                        ),
                    )
                }
                .onFailure {
                    publish(ChatState(conversationId = conversationId))
                }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        val current = state
        if (current.streamingText.isNotBlank()) {
            scope.launch {
                repository.appendMessage(
                    current.conversationId,
                    MessageRole.Assistant,
                    current.streamingText,
                )
            }
            publish(
                current.copy(
                    turns = current.turns + ChatTurn("assistant", current.streamingText),
                    streamingText = "",
                    loading = false,
                ),
            )
        } else {
            publish(current.copy(loading = false, streamingText = ""))
        }
    }

    fun send(text: String) {
        val provider = activeProvider() ?: run {
            publish(state.copy(errorText = "no provider"))
            return
        }
        val model = activeModel() ?: run {
            publish(state.copy(errorText = "no model"))
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
            ),
        )

        scope.launch {
            var conversationId = current.conversationId
            if (conversationId.isBlank()) {
                conversationId = repository.createConversation(titleOf(text))
                publish(state.copy(conversationId = conversationId))
            }
            repository.appendMessage(conversationId, MessageRole.User, text)
            repository.updateConversationTitleFromFirstMessage(conversationId, text)

            val profile = AssistantStore.load(context)
            val request = ChatRequest(
                provider = provider,
                model = model,
                messages = nextTurns,
                systemPrompt = AssistantStore.buildSystemPrompt(profile),
            )
            startStreaming(conversationId, request)
        }
    }

    fun retry() {
        val current = state
        val lastUser = current.turns.lastOrNull { it.role == "user" } ?: return
        val trimmed = current.turns.dropLastWhile { it.role != "user" }
        publish(current.copy(turns = trimmed, errorText = "", streamingText = "", loading = true))
        val provider = activeProvider() ?: return
        val model = activeModel() ?: return
        val profile: AssistantProfile = AssistantStore.load(context)
        val request = ChatRequest(
            provider = provider,
            model = model,
            messages = trimmed,
            systemPrompt = AssistantStore.buildSystemPrompt(profile),
        )
        if (lastUser.content.isBlank()) return
        startStreaming(current.conversationId, request)
    }

    private fun startStreaming(conversationId: String, request: ChatRequest) {
        job?.cancel()
        job = scope.launch {
            val builder = StringBuilder()
            try {
                ChatClient.send(request).collect { chunk ->
                    when (chunk) {
                        is ChatChunk.Delta -> {
                            builder.append(chunk.text)
                            publish(
                                state.copy(
                                    streamingText = builder.toString(),
                                    loading = false,
                                ),
                            )
                        }

                        is ChatChunk.Usage -> Unit
                        is ChatChunk.ToolCallDelta -> Unit
                        is ChatChunk.Finish -> Unit
                        ChatChunk.Done -> Unit
                    }
                }
                val finalText = builder.toString()
                if (finalText.isNotBlank()) {
                    repository.appendMessage(conversationId, MessageRole.Assistant, finalText)
                }
                publish(
                    state.copy(
                        turns = state.turns + ChatTurn("assistant", finalText),
                        streamingText = "",
                        loading = false,
                    ),
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: ChatException) {
                val finalText = builder.toString()
                publish(
                    state.copy(
                        turns = if (finalText.isBlank()) {
                            state.turns
                        } else {
                            state.turns + ChatTurn("assistant", finalText)
                        },
                        streamingText = "",
                        loading = false,
                        errorText = readable(e.error),
                    ),
                )
            } catch (e: Exception) {
                publish(
                    state.copy(
                        streamingText = "",
                        loading = false,
                        errorText = e.message ?: "unknown",
                    ),
                )
            }
        }
    }

    private fun readable(error: ChatError): String = when (error) {
        is ChatError.Http -> "HTTP ${error.code} · ${error.detail}"
        is ChatError.Network -> error.message
        is ChatError.InvalidResponse -> error.message
        is ChatError.Cancelled -> error.message
    }

    private fun titleOf(text: String): String =
        text.trim().lineSequence().firstOrNull().orEmpty().take(28).ifBlank { "New chat" }

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
