package com.nova.app.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.NovaSwitch
import com.nova.app.ui.theme.NovaTheme

@Composable
fun PersonalizeScreen(
    autoCollapseThinking: Boolean,
    onAutoCollapseThinkingChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = NovaTheme.colors.card,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = NovaStrings.strings.autoCollapse,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NovaTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                NovaSwitch(
                    checked = autoCollapseThinking,
                    onCheckedChange = onAutoCollapseThinkingChange,
                )
            }
        }
        Text(
            text = NovaStrings.strings.autoCollapseHint,
            style = MaterialTheme.typography.bodyMedium,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(start = 18.dp, top = 8.dp),
        )
        Spacer(Modifier.height(24.dp))
    }
}
