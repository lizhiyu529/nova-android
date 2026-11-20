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
        Surface(
            onClick = onAddProvider,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = NovaTheme.colors.card,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(30.dp),
                    shape = CircleShape,
                    color = NovaTheme.colors.accent,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = NovaIcons.Plus,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = NovaTheme.colors.onAccent,
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = strings.providersAdd,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = NovaTheme.colors.textPrimary,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (providers.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = NovaIcons.Database,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = NovaTheme.colors.textDisabled,
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = strings.providersEmpty,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NovaTheme.colors.textSecondary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = strings.providersEmptyHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = NovaTheme.colors.textTertiary,
                )
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = NovaTheme.colors.card,
            ) {
                Column {
                    providers.forEachIndexed { index, provider ->
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 60.dp)
                                    .height(0.7.dp),
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = NovaTheme.colors.divider,
                                ) {}
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
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun ProviderRow(
    provider: Provider,
    active: Boolean,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(10.dp),
                color = NovaTheme.colors.containerHigh,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = provider.name.take(1).uppercase().ifBlank { "?" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NovaTheme.colors.textSecondary,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = provider.name.ifBlank { "-" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = NovaTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (provider.endpoint.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = provider.endpoint,
                        style = MaterialTheme.typography.bodySmall,
                        color = NovaTheme.colors.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (active) {
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NovaTheme.colors.accentSubtle,
                ) {
                    Text(
                        text = NovaStrings.strings.confirm,
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaTheme.colors.onAccentSubtle,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }
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
