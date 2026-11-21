package com.nova.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
        Surface(
            onClick = onAddProvider,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = NovaTheme.colors.accent,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = NovaIcons.Plus,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = NovaTheme.colors.onAccent,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = strings.providersAdd,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = NovaTheme.colors.onAccent,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = NovaIcons.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = NovaTheme.colors.onAccent,
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        if (providers.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    modifier = Modifier.size(54.dp),
                    shape = CircleShape,
                    color = NovaTheme.colors.containerLow,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = NovaIcons.Database,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
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
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                providers.forEach { provider ->
                    ProviderCard(
                        provider = provider,
                        active = provider.id == activeId,
                        onClick = { onOpenProvider(provider.id) },
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun ProviderCard(
    provider: Provider,
    active: Boolean,
    onClick: () -> Unit,
) {
    val strings = NovaStrings.strings
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(20.dp),
            color = NovaTheme.colors.card,
            border = if (active) {
                BorderStroke(1.4.dp, NovaTheme.colors.accent)
            } else {
                BorderStroke(0.8.dp, NovaTheme.colors.divider)
            },
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(13.dp),
                        color = if (active) {
                            NovaTheme.colors.accent
                        } else {
                            NovaTheme.colors.containerHigh
                        },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = provider.name.take(1).uppercase().ifBlank { "?" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (active) {
                                    NovaTheme.colors.onAccent
                                } else {
                                    NovaTheme.colors.textSecondary
                                },
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = provider.name.ifBlank { "-" },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = NovaTheme.colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            if (active) {
                                Spacer(Modifier.width(7.dp))
                                Surface(
                                    shape = RoundedCornerShape(7.dp),
                                    color = NovaTheme.colors.accentSubtle,
                                ) {
                                    Text(
                                        text = strings.confirm,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NovaTheme.colors.onAccentSubtle,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                            }
                        }
                        if (provider.endpoint.isNotBlank()) {
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = provider.endpoint,
                                style = MaterialTheme.typography.bodySmall,
                                color = NovaTheme.colors.textTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = NovaIcons.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = NovaTheme.colors.textTertiary,
                    )
                }
                if (provider.models.isNotEmpty()) {
                    Spacer(Modifier.height(11.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ModelChip(provider.models.size.toString() + " models")
                        if (provider.allowHttp) {
                            ModelChip("HTTP")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = NovaTheme.colors.containerLow,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = NovaTheme.colors.textTertiary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}
