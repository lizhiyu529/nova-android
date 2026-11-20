package com.nova.app.data.provider

data class ProviderModel(
    val id: String,
    val latencyMs: Long = -1L,
    val reachable: Boolean = false,
) {
    val tested: Boolean get() = latencyMs >= 0L
}

data class Provider(
    val id: String,
    val name: String,
    val apiKey: String = "",
    val endpoint: String = "",
    val allowHttp: Boolean = false,
    val models: List<ProviderModel> = emptyList(),
    val createdAt: Long = 0L,
)

fun normalizeEndpoint(raw: String, allowHttp: Boolean): String {
    val trimmed = raw.trim().trimEnd('/')
    if (trimmed.isEmpty()) return ""
    val hasScheme = trimmed.startsWith("http://", true) || trimmed.startsWith("https://", true)
    val scheme = if (allowHttp) "http://" else "https://"
    val withScheme = if (hasScheme) trimmed else scheme + trimmed

    val bare = withScheme.substringAfter("://")
    val hostAndPath = bare.trim('/')
    if (hostAndPath.isEmpty()) return ""

    val firstSegment = hostAndPath.substringBefore('/')
    val rest = hostAndPath.substringAfter('/', "")
    val hasVersionSuffix = firstSegment.endsWith("/v1") ||
        rest.startsWith("v1/") || rest == "v1"

    val base = withScheme.trimEnd('/')
    return if (hasVersionSuffix) base else "$base/v1"
}

fun modelsUrl(endpoint: String): String = "${endpoint.trimEnd('/')}/models"

fun chatUrl(endpoint: String): String = "${endpoint.trimEnd('/')}/chat/completions"
