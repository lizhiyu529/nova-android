package com.nova.app.data.provider

enum class ReasoningEffort(val key: String) {
    Default(""),
    None("none"),
    Minimal("minimal"),
    Low("low"),
    Medium("medium"),
    High("high"),
    ExtraHigh("xhigh"),
    Max("max"),
    ;

    companion object {
        fun fromKey(key: String?): ReasoningEffort =
            entries.firstOrNull { it.key == key } ?: Default
    }
}

data class GenerationSettings(
    val reasoning: ReasoningEffort = ReasoningEffort.Default,
    val stream: Boolean = true,
    val toolsEnabled: Boolean = true,
    val maxToolRounds: Int = 3,
    val autoTitle: Boolean = true,
)
