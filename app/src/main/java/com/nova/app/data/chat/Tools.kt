package com.nova.app.data.chat

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONObject

object NovaTools {

    const val TIME = "get_current_time"
    const val CALCULATOR = "calculator"
    const val WORD_COUNT = "word_count"

    val definitions: List<ChatTool> = listOf(
        ChatTool(
            name = TIME,
            description = "Get the current local date and time with timezone.",
            parameters = """
                {"type":"object","properties":{},"additionalProperties":false}
            """.trimIndent(),
        ),
        ChatTool(
            name = CALCULATOR,
            description = "Evaluate a basic arithmetic expression with + - * / % and parentheses.",
            parameters = """
                {
                  "type":"object",
                  "properties":{
                    "expression":{"type":"string","description":"Arithmetic expression such as (2+3)*4"}
                  },
                  "required":["expression"],
                  "additionalProperties":false
                }
            """.trimIndent(),
        ),
        ChatTool(
            name = WORD_COUNT,
            description = "Count characters words and lines of a piece of text.",
            parameters = """
                {
                  "type":"object",
                  "properties":{
                    "text":{"type":"string","description":"Text to inspect"}
                  },
                  "required":["text"],
                  "additionalProperties":false
                }
            """.trimIndent(),
        ),
    )

    fun execute(name: String, arguments: String): String {
        val args = runCatching { JSONObject(arguments.ifBlank { "{}" }) }.getOrElse { JSONObject() }
        return when (name) {
            TIME -> {
                val now = Date()
                val full = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault()).format(now)
                val zone = java.util.TimeZone.getDefault().id
                """{"datetime":"$full","timezone":"$zone"}"""
            }

            CALCULATOR -> {
                val expression = args.optString("expression", "")
                val value = runCatching { Calculator.evaluate(expression) }.getOrNull()
                if (value == null) {
                    """{"error":"invalid expression"}"""
                } else {
                    """{"expression":"${escape(expression)}","result":$value}"""
                }
            }

            WORD_COUNT -> {
                val text = args.optString("text", "")
                val words = text.split(Regex("\\s+")).count { it.isNotBlank() }
                """{"characters":${text.length},"words":$words,"lines":${text.lines().size}}"""
            }

            else -> """{"error":"unknown tool"}"""
        }
    }

    private fun escape(value: String): String =
        value.replace("\\", "\\\\").replace("\"", "\\\"")

    fun displayName(name: String): String = when (name) {
        TIME -> "get_current_time"
        CALCULATOR -> "calculator"
        WORD_COUNT -> "word_count"
        else -> name
    }
}

object Calculator {

    fun evaluate(expression: String): Double? {
        val parser = Parser(expression.filterNot { it.isWhitespace() })
        val value = parser.parseExpression()
        return if (parser.atEnd()) value else null
    }

    private class Parser(private val text: String) {
        private var pos = 0

        fun atEnd(): Boolean = pos >= text.length

        fun parseExpression(): Double {
            var value = parseTerm()
            while (!atEnd()) {
                when (text[pos]) {
                    '+' -> {
                        pos++
                        value += parseTerm()
                    }

                    '-' -> {
                        pos++
                        value -= parseTerm()
                    }

                    else -> return value
                }
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (!atEnd()) {
                when (text[pos]) {
                    '*' -> {
                        pos++
                        value *= parseFactor()
                    }

                    '/' -> {
                        pos++
                        val divisor = parseFactor()
                        value = if (divisor == 0.0) Double.NaN else value / divisor
                    }

                    '%' -> {
                        pos++
                        value %= parseFactor()
                    }

                    else -> return value
                }
            }
            return value
        }

        private fun parseFactor(): Double {
            if (atEnd()) return Double.NaN
            val current = text[pos]
            if (current == '-') {
                pos++
                return -parseFactor()
            }
            if (current == '+') {
                pos++
                return parseFactor()
            }
            if (current == '(') {
                pos++
                val value = parseExpression()
                if (!atEnd() && text[pos] == ')') pos++
                return value
            }
            val start = pos
            while (!atEnd() && (text[pos].isDigit() || text[pos] == '.')) pos++
            if (start == pos) return Double.NaN
            return text.substring(start, pos).toDoubleOrNull() ?: Double.NaN
        }
    }
}
