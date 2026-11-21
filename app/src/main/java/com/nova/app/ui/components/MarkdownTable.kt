package com.nova.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nova.app.ui.theme.NovaTheme

data class TableData(
    val header: List<String>,
    val rows: List<List<String>>,
)

fun isTableSeparator(line: String): Boolean {
    val trimmed = line.trim()
    if (!trimmed.contains('-')) return false
    if (!trimmed.startsWith("|") && !trimmed.contains("|")) return false
    return trimmed.split('|')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .all { cell -> cell.matches(Regex("^:?-{1,}:?$")) }
}

fun parseTableRow(line: String): List<String> {
    var trimmed = line.trim()
    if (trimmed.startsWith("|")) trimmed = trimmed.drop(1)
    if (trimmed.endsWith("|")) trimmed = trimmed.dropLast(1)
    return trimmed.split('|').map { it.trim() }
}

@Composable
fun MarkdownTable(table: TableData, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = NovaTheme.colors.containerLow,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
        ) {
            Column(modifier = Modifier.widthIn(min = 240.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NovaTheme.colors.containerHigh),
                ) {
                    table.header.forEachIndexed { index, cell ->
                        TableCell(
                            text = cell,
                            header = true,
                            showDivider = index > 0,
                        )
                    }
                }
                table.rows.forEachIndexed { rowIndex, row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (rowIndex > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NovaTheme.colors.divider),
                            )
                        }
                        row.forEachIndexed { index, cell ->
                            TableCell(
                                text = cell,
                                header = false,
                                showDivider = index > 0,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    header: Boolean,
    showDivider: Boolean,
) {
    Row {
        if (showDivider) {
            Box(
                modifier = Modifier
                    .widthIn(min = 0.dp)
                    .background(NovaTheme.colors.divider),
            ) {
                Surface(
                    modifier = Modifier.widthIn(min = 0.7.dp),
                    color = NovaTheme.colors.divider,
                ) {}
            }
        }
        Text(
            text = text,
            style = if (header) {
                MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (header) NovaTheme.colors.textPrimary else NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
        )
    }
}
