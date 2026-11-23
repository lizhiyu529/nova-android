package com.nova.app.data.chat

import android.content.Context
import com.nova.app.data.AssistantStore
import com.nova.app.data.ConversationRepository
import com.nova.app.data.provider.GenerationSettings
import com.nova.app.data.provider.GenerationStore
import com.nova.app.data.provider.Provider
import com.nova.app.data.provider.ProviderStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ChatState(
    val messages: List<UIMessage> = emptyList(),
    val streaming: UIMessage? = null,
    val busy: Boolean = false,
    val errorText: String = "",
    val conversationId: String = "",
    val retryAvailable: Boolean = false,
) {
    val loading: Boolean
        get() = busy && (streaming == null || streaming.parts.isEmptyUIMessage())

    val displayMessages: List<UIMessage>
        get() {
            val live = streaming ?: return messages
            if (live.parts.isEmptyUIMessage()) return messages
            val head = messages.filterNot { it.id == live.id }
            return head + live
        }
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
        publish(ChatState(conversationId = conversationId, busy = true))
        scope.launch {
            val stored = runCatching { repository.uiMessagesOf(conversationId) }.getOrDefault(emptyList())
            publish(
                ChatState(
                    conversationId = conversationId,
                    messages = stored,
                    retryAvailable = stored.any { it.role == MessageRole.USER },
                ),
            )
        }
    }

    fun stop() {
        val current = state
        val streaming = current.streaming
        job?.cancel()
        job = null
        if (streaming != null && !streaming.parts.isEmptyUIMessage()) {
            val settled = streaming
                .finishPendingTools { part ->
                    part.copy(output = STOPPED_OUTPUT, isExecuted = true)
                }
                .finishReasoning()
            publish(
                current.copy(
                    messages = current.messages + settled,
                    streaming = null,
                    busy = false,
                    retryAvailable = true,
                ),
            )
            scope.launch {
                runCatching { repository.appendUIMessage(current.conversationId, settled) }
            }
        } else {
            publish(current.copy(streaming = null, busy = false))
        }
    }

    fun send(text: String) {
        if (text.isBlank()) return
        val provider = activeProvider()
        if (provider == null) {
            publish(state.copy(errorText = "未配置提供商", busy = false))
            return
        }
        val model = activeModel(provider)
        if (model == null) {
            publish(state.copy(errorText = "未选择模型", busy = false))
            return
        }

        val userMessage = UIMessage(
            role = MessageRole.USER,
            parts = listOf(MessagePart.Text(text)),
            finished = true,
        )
        val next = state.messages + userMessage
        publish(
            state.copy(
                messages = next,
                streaming = null,
                busy = true,
                errorText = "",
                retryAvailable = false,
            ),
        )

        job?.cancel()
        job = scope.launch {
            var conversationId = state.conversationId
            if (conversationId.isBlank()) {
                conversationId = repository.createConversation(suggestTitle(text))
                publish(state.copy(conversationId = conversationId))
            }
            runCatching { repository.appendUIMessage(conversationId, userMessage) }
            runAgent(conversationId, provider, model, next)
        }
    }

    fun retry() {
        val current = state
        val lastUserIndex = current.messages.indexOfLast { it.role == MessageRole.USER }
        if (lastUserIndex < 0) return
        val trimmed = current.messages.take(lastUserIndex + 1)
        val provider = activeProvider() ?: return
        val model = activeModel(provider) ?: return
        publish(
            current.copy(
                messages = trimmed,
                streaming = null,
                errorText = "",
                busy = true,
                retryAvailable = false,
            ),
        )
        job?.cancel()
        job = scope.launch { runAgent(current.conversationId, provider, model, trimmed) }
    }

    private suspend fun runAgent(
        conversationId: String,
        provider: Provider,
        model: String,
        baseMessages: List<UIMessage>,
    ) {
        val settings = GenerationStore.load(context)
        val systemPrompt = AssistantStore.buildSystemPrompt(AssistantStore.load(context))
        val toolList = if (settings.toolsEnabled) NovaTools.definitions else emptyList()

        try {
            val loop = AgentLoop(
                maxSteps = settings.maxToolRounds + 1,
                tools = toolList,
                executor = object : ToolExecutor {
                    override suspend fun execute(name: String, arguments: String): String =
                        NovaTools.execute(name, arguments)
                },
                generate = { messages, base ->
                    if (settings.stream) {
                        streamGenerate(provider, model, messages, base, systemPrompt, settings, toolList)
                    } else {
                        generateOnce(provider, model, messages, base, systemPrompt, settings, toolList)
                    }
                },
                onMessage = { message ->
                    publish(state.copy(streaming = message, busy = true))
                },
            )

            val result = loop.run(baseMessages)
            if (result != null) {
                runCatching { repository.appendUIMessage(conversationId, result) }
                publish(
                    state.copy(
                        messages = baseMessages.filterNot { it.id == result.id } + result,
                        streaming = null,
                        busy = false,
                        retryAvailable = true,
                    ),
                )

                if (settings.autoTitle) {
                    val title = runCatching {
                        TitleGenerator.generate(context, provider, model, baseMessages + result)
                    }.getOrNull()
                        ?: TitleGenerator.fallbackTitle(baseMessages)
                    if (title.isNotBlank()) {
                        runCatching { repository.rename(conversationId, title) }
                        onTitleChanged(conversationId, title)
                    }
                }
            } else {
                publish(state.copy(busy = false, streaming = null))
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (e: ChatException) {
            publish(state.copy(streaming = null, busy = false, errorText = readable(e.error), retryAvailable = true))
        } catch (e: Exception) {
            publish(
                state.copy(
                    streaming = null,
                    busy = false,
                    errorText = e.message ?: e.javaClass.simpleName,
                    retryAvailable = true,
                ),
            )
        }
    }

    private suspend fun streamGenerate(
        provider: Provider,
        model: String,
        messages: List<UIMessage>,
        base: UIMessage,
        systemPrompt: String,
        settings: GenerationSettings,
        tools: List<ChatTool>,
    ): UIMessage {
        val handler = StreamChunkHandler()
        var current = base
        var lastPublish = 0L
        val startedAt = System.currentTimeMillis()

        OpenAiClient.stream(
            ChatRequest(
                provider = provider,
                model = model,
                messages = messages,
                systemPrompt = systemPrompt,
                reasoning = settings.reasoning.key.ifBlank { null },
                tools = tools,
                stream = true,
            ),
        ).collect { chunk ->
            current = handler.handle(listOf(current), chunk).single()
            val now = System.currentTimeMillis()
            if (now - lastPublish >= PUBLISH_INTERVAL_MS) {
                lastPublish = now
                publish(
                    state.copy(
                        streaming = current.copy(
                            finished = false,
                            elapsedMs = now - startedAt,
                        ),
                        busy = true,
                    ),
                )
            }
        }
        return current.copy(
            parts = current.parts,
            finished = true,
            steps = base.steps + 1,
            elapsedMs = System.currentTimeMillis() - startedAt,
        ).finishReasoning()
    }

    private suspend fun generateOnce(
        provider: Provider,
        model: String,
        messages: List<UIMessage>,
        base: UIMessage,
        systemPrompt: String,
        settings: GenerationSettings,
        tools: List<ChatTool>,
    ): UIMessage {
        val startedAt = System.currentTimeMillis()
        var result: UIMessage? = null
        OpenAiClient.generate(
            ChatRequest(
                provider = provider,
                model = model,
                messages = messages,
                systemPrompt = systemPrompt,
                reasoning = settings.reasoning.key.ifBlank { null },
                tools = tools,
                stream = false,
            ),
        ).collect { result = it }
        val generated = result ?: UIMessage(role = MessageRole.ASSISTANT, parts = emptyList(), finished = true)
        return base.appendParts(generated.parts).copy(
            usage = base.usage.sum(generated.usage),
            lastUsage = generated.usage,
            elapsedMs = System.currentTimeMillis() - startedAt,
        )
    }

    private fun suggestTitle(text: String): String {
        val firstLine = text.trim().lineSequence().firstOrNull().orEmpty()
        return firstLine.take(20).ifBlank { "新对话" }
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

    private companion object {
        const val PUBLISH_INTERVAL_MS = 60L
        const val STOPPED_OUTPUT = "{\"error\":\"stopped by user\"}"
    }
}
