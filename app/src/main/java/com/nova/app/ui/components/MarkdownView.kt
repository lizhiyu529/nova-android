package com.nova.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.nova.app.ui.theme.NovaTheme

@Composable
fun MarkdownView(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = NovaTheme.colors
    val typography = MaterialTheme.typography

    Markdown(
        content = text,
        modifier = modifier.fillMaxWidth(),
        colors = markdownColor(
            text = colors.textPrimary,
            codeText = colors.textPrimary,
            codeBackground = colors.inputBackground,
            inlineCodeText = colors.textPrimary,
            inlineCodeBackground = colors.containerHigh,
            dividerColor = colors.divider,
            tableText = colors.textPrimary,
            tableBackground = colors.containerLow,
        ),
        typography = markdownTypography(
            h1 = typography.headlineSmall,
            h2 = typography.titleLarge,
            h3 = typography.titleMedium,
            h4 = typography.titleSmall,
            h5 = typography.bodyLarge,
            h6 = typography.bodyMedium,
            text = typography.bodyLarge,
            code = typography.bodySmall,
            inlineCode = typography.bodySmall,
            quote = typography.bodyMedium,
            paragraph = typography.bodyLarge,
            ordered = typography.bodyLarge,
            bullet = typography.bodyLarge,
            list = typography.bodyLarge,
            table = typography.bodyMedium,
            textLink = typography.bodyLarge,
        ),
    )
}

@Composable
fun StreamingMarkdownView(
    text: String,
    modifier: Modifier = Modifier,
) {
    if (text.isBlank()) return
    MarkdownView(text = text, modifier = modifier)
}
