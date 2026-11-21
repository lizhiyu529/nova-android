package com.nova.app.data.chat

import org.json.JSONObject

fun JSONObject.str(key: String): String {
    if (isNull(key)) return ""
    val value = opt(key) ?: return ""
    if (value == JSONObject.NULL) return ""
    val text = value.toString()
    return if (text == "null") "" else text
}

fun JSONObject.obj(key: String): JSONObject? {
    if (isNull(key)) return null
    return optJSONObject(key)
}
