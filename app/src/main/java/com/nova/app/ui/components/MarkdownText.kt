package com.nova.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.nova.app.ui.theme.NovaTheme

private data class Block(
    val kind: Kind,
    val text: String,
    val level: Int = 0,
    val table: TableData? = null,
) {
    enum class Kind { Paragraph, Heading, Bullet, Ordered, Quote, Code, Divider, Spacer, Table }
}

private fun parseBlocks(source: String): List<Block> {
    val blocks = mutableListOf<Block>()
    val lines = source.replace("\r\n", "\n").split("\n")
    val paragraph = StringBuilder()
    val code = StringBuilder()
    var inCode = false

    fun flushParagraph() {
        if (paragraph.isNotBlank()) {
            blocks.add(Block(Block.Kind.Paragraph, paragraph.toString().trim()))
        }
        paragraph.clear()
    }

    var index = 0
    while (index < lines.size) {
        val line = lines[index]
        val trimmed = line.trimStart()

        if (trimmed.startsWith("```")) {
            if (inCode) {
                blocks.add(Block(Block.Kind.Code, code.toString().trimEnd()))
                code.clear()
                inCode = false
            } else {
                flushParagraph()
                inCode = true
            }
            index++
            continue
        }

        if (inCode) {
            code.appendLine(line)
            index++
            continue
        }

        val next = lines.getOrNull(index + 1)
        if (trimmed.contains('|') && next != null && isTableSeparator(next)) {
            flushParagraph()
            val header = parseTableRow(trimmed)
            val rows = mutableListOf<List<String>>()
            var scan = index + 2
            while (scan < lines.size) {
                val candidate = lines[scan].trim()
                if (candidate.isEmpty() || !candidate.contains('|')) break
                rows.add(parseTableRow(candidate))
                scan++
            }
            blocks.add(
                Block(
                    kind = Block.Kind.Table,
                    text = "",
                    table = TableData(header = header, rows = rows),
                ),
            )
            index = scan
            continue
        }

        when {
            line.isBlank() -> {
                flushParagraph()
                if (blocks.lastOrNull()?.kind != Block.Kind.Spacer) {
                    blocks.add(Block(Block.Kind.Spacer, ""))
                }
            }

            trimmed.startsWith("#") -> {
                flushParagraph()
                val level = trimmed.takeWhile { it == '#' }.length.coerceAtMost(6)
                blocks.add(Block(Block.Kind.Heading, trimmed.drop(level).trim(), level))
            }

            trimmed.startsWith("> ") -> {
                flushParagraph()
                blocks.add(Block(Block.Kind.Quote, trimmed.removePrefix("> ").trim()))
            }

            trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("+ ") -> {
                flushParagraph()
                blocks.add(Block(Block.Kind.Bullet, trimmed.drop(2).trim()))
            }

            Regex("^\\d+[.)]\\s+").containsMatchIn(trimmed) -> {
                flushParagraph()
                val number = trimmed.takeWhile { it.isDigit() }
                blocks.add(Block(Block.Kind.Ordered, trimmed.drop(number.length + 1).trim()))
            }

            trimmed == "---" || trimmed == "***" || trimmed == "___" -> {
                flushParagraph()
                blocks.add(Block(Block.Kind.Divider, ""))
            }

            else -> {
                if (paragraph.isNotEmpty()) paragraph.append(' ')
                paragraph.append(trimmed)
            }
        }
        index++
    }
    if (inCode && code.isNotBlank()) {
        blocks.add(Block(Block.Kind.Code, code.toString().trimEnd()))
    }
    flushParagraph()
    return blocks.filterNot { it.kind == Block.Kind.Spacer }.ifEmpty { emptyList() }
}

private fun inline(source: String, codeBackground: androidx.compose.ui.graphics.Color): AnnotatedString = buildAnnotatedString {
    var index = 0
    val buffer = StringBuilder()

    fun flushBuffer() {
        if (buffer.isNotEmpty()) {
            append(buffer.toString())
            buffer.clear()
        }
    }

    while (index < source.length) {
        when {
            source.startsWith("**", index) || source.startsWith("__", index) -> {
                val marker = source.substring(index, index + 2)
                val end = source.indexOf(marker, index + 2)
                if (end > 0) {
                    flushBuffer()
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(source.substring(index + 2, end))
                    }
                    index = end + 2
                } else {
                    buffer.append(source[index])
                    index++
                }
            }

            source.startsWith("`", index) -> {
                val end = source.indexOf('`', index + 1)
                if (end > 0) {
                    flushBuffer()
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = codeBackground,
                        ),
                    ) {
                        append(" " + source.substring(index + 1, end) + " ")
                    }
                    index = end + 1
                } else {
                    buffer.append(source[index])
                    index++
                }
            }

            source.startsWith("*", index) || source.startsWith("_", index) -> {
                val marker = source[index]
                val end = source.indexOf(marker, index + 1)
                if (end > 0 && end - index > 1) {
                    flushBuffer()
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(source.substring(index + 1, end))
                    }
                    index = end + 1
                } else {
                    buffer.append(source[index])
                    index++
                }
            }

            else -> {
                buffer.append(source[index])
                index++
            }
        }
    }
    flushBuffer()
}

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val codeBackground = NovaTheme.colors.containerHigh
    val blocks = remember(text) { parseBlocks(text) }
    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEach { block ->
            when (block.kind) {
                Block.Kind.Heading -> {
                    val style = when (block.level) {
                        1 -> MaterialTheme.typography.headlineSmall
                        2 -> MaterialTheme.typography.titleLarge
                        else -> MaterialTheme.typography.titleMedium
                    }
                    Text(
                        text = inline(block.text, codeBackground),
                        style = style.copy(fontWeight = FontWeight.SemiBold),
                        color = NovaTheme.colors.textPrimary,
                        modifier = Modifier.padding(top = 6.dp, bottom = 3.dp),
                    )
                }

                Block.Kind.Bullet -> {
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NovaTheme.colors.textTertiary,
                            modifier = Modifier.width(16.dp),
                        )
                        Text(
                            text = inline(block.text, codeBackground),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NovaTheme.colors.textPrimary,
                        )
                    }
                }

                Block.Kind.Ordered -> {
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(
                            text = "◦",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NovaTheme.colors.textTertiary,
                            modifier = Modifier.width(16.dp),
                        )
                        Text(
                            text = inline(block.text, codeBackground),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NovaTheme.colors.textPrimary,
                        )
                    }
                }

                Block.Kind.Quote -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = NovaTheme.colors.containerLow,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(20.dp)
                                    .background(NovaTheme.colors.border),
                            )
                            Text(
                                text = inline(block.text, codeBackground),
                                style = MaterialTheme.typography.bodyMedium,
                                color = NovaTheme.colors.textSecondary,
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp,
                                ),
                            )
                        }
                    }
                }

                Block.Kind.Code -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = NovaTheme.colors.inputBackground,
                    ) {
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                            ),
                            color = NovaTheme.colors.textPrimary,
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
                        )
                    }
                }

                Block.Kind.Table -> {
                    block.table?.let { MarkdownTable(it, codeBackground = codeBackground) }
                }

                Block.Kind.Divider -> {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .width(1.dp)
                            .background(NovaTheme.colors.divider),
                    )
                }

                else -> {
                    Text(
                        text = inline(block.text, codeBackground),
                        style = MaterialTheme.typography.bodyLarge,
                        color = NovaTheme.colors.textPrimary,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
            }
        }
    }
}
