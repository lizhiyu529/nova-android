package com.nova.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nova.app.data.provider.Provider
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.theme.NovaTheme

@Composable
fun ProvidersScreen(
    providers: List<Provider>,
    activeId: String,
    onOpenProvider: (String) -> Unit,
    onAddProvider: () -> Unit,
) {
    val strings = NovaStrings.strings
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        AddCard(onClick = onAddProvider, label = strings.providersAdd)

        Spacer(Modifier.height(20.dp))

        if (providers.isEmpty()) {
            EmptyProviders()
        } else {
            ProviderGroup(
                providers = providers,
                activeId = activeId,
                onOpenProvider = onOpenProvider,
            )
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun AddCard(onClick: () -> Unit, label: String) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = NovaTheme.colors.accent,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = NovaIcons.Plus,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = NovaTheme.colors.onAccent,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = NovaTheme.colors.onAccent,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProviderGroup(
    providers: List<Provider>,
    activeId: String,
    onOpenProvider: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NovaTheme.colors.card,
    ) {
        Column {
            providers.forEachIndexed { index, provider ->
                if (index > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 62.dp)
                            .height(0.7.dp),
                    ) {
                        Surface(modifier = Modifier.fillMaxSize(), color = NovaTheme.colors.divider) {}
                    }
                }
                ProviderRow(
                    provider = provider,
                    active = provider.id == activeId,
                    onClick = { onOpenProvider(provider.id) },
                )
            }
        }
    }
}

@Composable
private fun ProviderRow(
    provider: Provider,
    active: Boolean,
    onClick: () -> Unit,
) {
    val strings = NovaStrings.strings
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(11.dp),
                color = if (active) NovaTheme.colors.accent else NovaTheme.colors.containerHigh,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = provider.name.take(1).uppercase().ifBlank { "?" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (active) NovaTheme.colors.onAccent else NovaTheme.colors.textSecondary,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = provider.name.ifBlank { "-" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = NovaTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (active) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NovaTheme.colors.accentSubtle,
                        ) {
                            Text(
                                text = strings.confirm,
                                style = MaterialTheme.typography.labelSmall,
                                color = NovaTheme.colors.onAccentSubtle,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = buildString {
                        if (provider.endpoint.isNotBlank()) append(provider.endpoint)
                        if (provider.models.isNotEmpty()) {
                            if (isNotEmpty()) append("  ·  ")
                            append(provider.models.size)
                            append(" models")
                        }
                    }.ifBlank { "-" },
                    style = MaterialTheme.typography.bodySmall,
                    color = NovaTheme.colors.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = NovaIcons.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = NovaTheme.colors.textTertiary,
            )
        }
    }
}

@Composable
private fun EmptyProviders() {
    val strings = NovaStrings.strings
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = NovaTheme.colors.containerLow,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = NovaIcons.Database,
                    contentDescription = null,
                    modifier = Modifier.size(23.dp),
                    tint = NovaTheme.colors.textDisabled,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = strings.providersEmpty,
            style = MaterialTheme.typography.bodyLarge,
            color = NovaTheme.colors.textSecondary,
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = strings.providersEmptyHint,
            style = MaterialTheme.typography.bodySmall,
            color = NovaTheme.colors.textTertiary,
        )
    }
}
