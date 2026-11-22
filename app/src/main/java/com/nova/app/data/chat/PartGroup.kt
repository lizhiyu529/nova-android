package com.nova.app.data.chat

sealed interface PartGroup {
    data class Content(val parts: List<ChatPart>) : PartGroup
    data class Tools(val tools: List<ChatPart.Tool>) : PartGroup
}

fun groupPartsByToolBoundary(parts: List<ChatPart>): List<PartGroup> {
    val groups = mutableListOf<PartGroup>()
    val currentContent = mutableListOf<ChatPart>()
    val currentTools = mutableListOf<ChatPart.Tool>()

    fun flushContent() {
        if (currentContent.isNotEmpty()) {
            groups.add(PartGroup.Content(currentContent.toList()))
            currentContent.clear()
        }
    }

    fun flushTools() {
        if (currentTools.isNotEmpty()) {
            groups.add(PartGroup.Tools(currentTools.toList()))
            currentTools.clear()
        }
    }

    for (part in parts) {
        if (part is ChatPart.Tool && part.isExecuted) {
            flushContent()
            currentTools.add(part)
        } else {
            flushTools()
            currentContent.add(part)
        }
    }

    flushContent()
    flushTools()
    return groups
}

fun List<ChatPart>.isEmptyInputMessage(): Boolean {
    if (isEmpty()) return true
    return all { part ->
        when (part) {
            is ChatPart.Text -> part.text.isBlank()
            else -> true
        }
    }
}

fun ChatTurn.finishReasoning(): ChatTurn {
    return copy(
        parts = parts.map { part ->
            if (part is ChatPart.Reasoning && !part.finished) {
                part.copy(finished = true)
            } else {
                part
            }
        },
    )
}

fun ChatTurn.finishPendingTools(
    transform: (ChatPart.Tool) -> ChatPart.Tool,
): ChatTurn {
    return copy(
        parts = parts.map { part ->
            if (part is ChatPart.Tool && !part.isExecuted) {
                transform(part)
            } else {
                part
            }
        },
    )
}
