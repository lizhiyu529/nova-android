package com.nova.app.data.chat

private const val EMPTY_TOOL_OUTPUT = "(no output)"

class AgentLoop(
    private val maxSteps: Int,
    private val tools: List<ChatTool>,
    private val executor: ToolExecutor,
    private val generate: suspend (List<UIMessage>, UIMessage) -> UIMessage,
    private val onMessage: suspend (UIMessage) -> Unit,
) {

    suspend fun run(initial: List<UIMessage>): UIMessage? {
        val history = initial.toMutableList()
        var message: UIMessage? = history.lastOrNull()
            ?.takeIf { it.role == MessageRole.ASSISTANT }
        if (message != null) history.removeAt(history.lastIndex)
        var steps = 0

        while (true) {
            val current = message
            if (current != null && current.getTools().any { !it.isExecuted }) {
                message = executeTools(current)
            } else if (current != null && steps > 0) {
                return message
            }

            if (steps >= maxSteps) return message

            val base = message ?: UIMessage(role = MessageRole.ASSISTANT, parts = emptyList())
            val requestMessages = if (message == null) history.toList() else history + base
            val generated = generate(requestMessages, base)
            message = generated
            onMessage(generated)
            steps++
        }
    }

    private suspend fun executeTools(message: UIMessage): UIMessage {
        var current = message
        val pending = current.getTools().filter { !it.isExecuted }
        for (tool in pending) {
            val output = runTool(tool)
            current = current.copy(
                parts = current.parts.map { part ->
                    if (part is MessagePart.Tool && part.toolCallId == tool.toolCallId) {
                        part.copy(output = output, isExecuted = true)
                    } else {
                        part
                    }
                },
            )
            onMessage(current)
        }
        return current
    }

    private suspend fun runTool(tool: MessagePart.Tool): String {
        return try {
            val known = tools.any { it.name == tool.toolName }
            if (!known) errorOutput("Tool ${tool.toolName} not found")
            else {
                val raw = executor.execute(tool.toolName, tool.input.ifBlank { "{}" })
                raw.ifBlank { EMPTY_TOOL_OUTPUT }
            }
        } catch (e: Exception) {
            errorOutput("[${e.javaClass.name}] ${e.message}")
        }
    }

    private fun errorOutput(error: String): String =
        org.json.JSONObject().put("error", error).toString()
}

interface ToolExecutor {
    suspend fun execute(name: String, arguments: String): String
}
