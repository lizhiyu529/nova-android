package com.nova.app.data.chat

class StreamState {

    private var sequence = 0
    private var textId: String? = null
    private var reasoningId: String? = null
    private val openToolIds = linkedSetOf<String>()
    private var lastToolId: String? = null

    fun appendText(text: String): List<ChatChunk> {
        if (text.isEmpty()) return emptyList()
        val out = mutableListOf<ChatChunk>()
        out.addAll(closeReasoning())
        out.addAll(closeTools())
        val id = textId ?: nextId("text").also {
            textId = it
            out.add(ChatChunk.TextStart(it))
        }
        out.add(ChatChunk.TextDelta(id, text))
        return out
    }

    fun appendReasoning(text: String): List<ChatChunk> {
        if (text.isEmpty()) return emptyList()
        val out = mutableListOf<ChatChunk>()
        out.addAll(closeText())
        out.addAll(closeTools())
        val id = reasoningId ?: nextId("reasoning").also {
            reasoningId = it
            out.add(ChatChunk.ReasoningStart(it))
        }
        out.add(ChatChunk.ReasoningDelta(id, text))
        return out
    }

    fun appendTool(
        toolCallId: String,
        toolName: String,
        input: String,
    ): List<ChatChunk> {
        val out = mutableListOf<ChatChunk>()
        out.addAll(closeText())
        out.addAll(closeReasoning())
        val id = toolCallId.ifBlank { lastToolId ?: nextId("tool") }
        var nameDelta = toolName
        if (openToolIds.add(id)) {
            nameDelta = ""
            out.add(ChatChunk.ToolCallStart(id, toolName))
        }
        lastToolId = id
        if (nameDelta.isNotEmpty() || input.isNotEmpty()) {
            out.add(ChatChunk.ToolCallDelta(id, nameDelta, input))
        }
        return out
    }

    fun finish(): List<ChatChunk> {
        val out = mutableListOf<ChatChunk>()
        out.addAll(closeText())
        out.addAll(closeReasoning())
        out.addAll(closeTools())
        out.add(ChatChunk.Done)
        return out
    }

    fun reset() {
        textId = null
        reasoningId = null
        openToolIds.clear()
        lastToolId = null
        sequence = 0
    }

    private fun closeText(): List<ChatChunk> {
        val id = textId ?: return emptyList()
        textId = null
        return listOf(ChatChunk.TextEnd(id))
    }

    private fun closeReasoning(): List<ChatChunk> {
        val id = reasoningId ?: return emptyList()
        reasoningId = null
        return listOf(ChatChunk.ReasoningEnd(id))
    }

    private fun closeTools(): List<ChatChunk> {
        if (openToolIds.isEmpty()) return emptyList()
        val out = openToolIds.map { ChatChunk.ToolCallEnd(it) }
        openToolIds.clear()
        lastToolId = null
        return out
    }

    private fun nextId(kind: String): String {
        sequence++
        return "$kind-$sequence"
    }
}
