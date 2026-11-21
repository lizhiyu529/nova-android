package com.nova.app.data.chat

import android.content.Context
import com.nova.app.data.provider.Provider
import com.nova.app.data.provider.ProviderStore
import kotlinx.coroutines.withTimeoutOrNull

object TitleGenerator {

    private const val TEMPLATE = """I will give you some dialogue content in the `<content>` block.
You need to summarize the conversation between user and assistant into a short title.
1. The title language should be consistent with the user's primary language
2. Do not use punctuation or other special symbols
3. Reply directly with the title
4. Summarize using 中文 (中国) language
5. The title should not exceed 10 characters

<content>
{content}
</content>"""

    suspend fun generate(
        context: Context,
        provider: Provider,
        model: String,
        turns: List<ChatTurn>,
    ): String? {
        if (turns.isEmpty()) return null
        val content = turns.takeLast(4)
            .joinToString("\n") { "${it.role}: ${it.content.take(300)}" }
        val prompt = TEMPLATE.replace("{content}", content)

        val result = withTimeoutOrNull(20_000L) {
            val builder = StringBuilder()
            runCatching {
                ChatClient.send(
                    ChatRequest(
                        provider = provider,
                        model = model,
                        messages = listOf(ChatTurn("user", prompt)),
                        stream = false,
                        maxTokens = 32,
                        temperature = 0.3,
                    ),
                ).collect { chunk ->
                    if (chunk is ChatChunk.TextDelta) builder.append(chunk.text)
                }
            }
            builder.toString()
        }

        return clean(result)
    }

    fun fallbackTitle(turns: List<ChatTurn>): String {
        val first = turns.firstOrNull { it.role == "user" }?.content.orEmpty()
        return sanitize(first).take(10).ifBlank { "新对话" }
    }

    private fun clean(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val firstLine = raw.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.isNotEmpty() }
            ?: return null
        val stripped = sanitize(firstLine)
        if (stripped.isBlank()) return null
        return stripped.take(10)
    }

    private fun sanitize(input: String): String {
        val cleaned = input.replace(Regex("[\\p{P}\\p{S}\\s]"), "")
        return cleaned.trim()
    }

    fun activeProvider(context: Context): Provider? {
        val providers = ProviderStore.load(context)
        val activeId = ProviderStore.activeId(context)
        return providers.firstOrNull { it.id == activeId } ?: providers.firstOrNull()
    }

    fun activeModel(context: Context, provider: Provider): String? {
        val selected = ProviderStore.activeModel(context, provider.id)
        if (selected.isNotBlank() && provider.models.any { it.id == selected }) return selected
        return provider.models.firstOrNull()?.id
    }
}
