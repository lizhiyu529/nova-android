package com.nova.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.ConfirmDialog
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.theme.NovaTheme

@Composable
fun OptionPickerDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var pending by remember { mutableStateOf(selectedIndex) }
    val windowHeight = LocalConfiguration.current.screenHeightDp
    val maxListHeight = (windowHeight * 0.52f).dp.coerceAtMost(380.dp)

    val windowWidth = LocalConfiguration.current.screenWidthDp
    val horizontalPadding = (windowWidth * 0.115f).dp.coerceIn(20.dp, 44.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            shape = RoundedCornerShape(26.dp),
            color = NovaTheme.colors.dialog,
            tonalElevation = 0.dp,
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NovaTheme.colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxListHeight),
                    color = Color.Transparent,
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .selectableGroup()
                            .padding(vertical = 2.dp),
                    ) {
                        options.forEachIndexed { index, label ->
                            OptionRow(
                                label = label,
                                selected = pending == index,
                                onClick = { pending = index },
                            )
                        }
                    }
                }
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                Surface(
                    onClick = { onConfirm(pending) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    color = Color.Transparent,
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = NovaStrings.strings.confirm,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = NovaTheme.colors.accent,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier.size(40.dp),
                colors = RadioButtonDefaults.colors(
                    selectedColor = NovaTheme.colors.textPrimary,
                    unselectedColor = NovaTheme.colors.textSecondary,
                ),
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = NovaTheme.colors.textPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp, vertical = 6.dp),
        )
    }
}

@Composable
fun SettingsScreen(
    language: AppLanguage,
    themeMode: AppThemeMode,
    versionName: String,
    onBack: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onLanguageNeedsRestart: () -> Unit,
    onProviders: () -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onFontSize: () -> Unit,
    onPersonalize: () -> Unit,
    onAccount: () -> Unit,
    onDataManagement: () -> Unit,
    onCheckUpdate: () -> Unit,
    onOpenLogs: () -> Unit,
) {
    var languageDialog by remember { mutableStateOf(false) }
    var restartPrompt by remember { mutableStateOf(false) }
    var themeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.size(6.dp))
        Text(
            text = NovaStrings.strings.sectionAccount,
            style = MaterialTheme.typography.labelLarge,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(start = 6.dp, top = 12.dp, bottom = 8.dp),
        )
        SettingsCard {
            SettingsRow(
                icon = NovaIcons.User,
                title = NovaStrings.strings.assistantCapabilities,
                onClick = onAccount,
            )
            SettingsRow(
                icon = NovaIcons.Database,
                title = NovaStrings.strings.dataManagement,
                onClick = onDataManagement,
            )
            SettingsRow(
                icon = NovaIcons.Sparkle,
                title = NovaStrings.strings.providersTitle,
                onClick = onProviders,
            )
        }

        Text(
            text = NovaStrings.strings.sectionApp,
            style = MaterialTheme.typography.labelLarge,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(start = 6.dp, top = 22.dp, bottom = 8.dp),
        )
        SettingsCard {
            SettingsRow(
                icon = NovaIcons.Globe,
                title = NovaStrings.strings.language,
                value = NovaStrings.strings.languageLabel(language),
                onClick = { languageDialog = true },
            )
            SettingsRow(
                icon = NovaIcons.Sun,
                title = NovaStrings.strings.appearance,
                value = NovaStrings.strings.themeLabel(themeMode),
                onClick = { themeDialog = true },
            )
            SettingsRow(
                icon = NovaIcons.FontSize,
                title = NovaStrings.strings.fontSize,
                onClick = onFontSize,
            )
            SettingsRow(
                icon = NovaIcons.Sparkle,
                title = NovaStrings.strings.personalization,
                onClick = onPersonalize,
            )
        }

        Text(
            text = NovaStrings.strings.sectionAbout,
            style = MaterialTheme.typography.labelLarge,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(start = 6.dp, top = 22.dp, bottom = 8.dp),
        )
        SettingsCard {
            SettingsRow(
                icon = NovaIcons.Info,
                title = NovaStrings.strings.checkUpdate,
                value = versionName,
                onClick = onCheckUpdate,
            )
            SettingsRow(
                icon = NovaIcons.Document,
                title = NovaStrings.strings.runtimeLogs,
                onClick = onOpenLogs,
            )
        }

        Text(
            text = NovaStrings.strings.disclaimer,
            style = MaterialTheme.typography.bodySmall,
            color = NovaTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 24.dp),
        )
    }

    if (languageDialog) {
        OptionPickerDialog(
            title = NovaStrings.strings.language,
            options = AppLanguage.entries.map { NovaStrings.strings.languageLabel(it) },
            selectedIndex = AppLanguage.entries.indexOf(language),
            onConfirm = { index ->
                val next = AppLanguage.entries[index]
                val changed = next != language
                onLanguageChange(next)
                languageDialog = false
                if (changed && next != AppLanguage.System) restartPrompt = true
            },
            onDismiss = { languageDialog = false },
        )
    }

    if (restartPrompt) {
        ConfirmDialog(
            title = NovaStrings.strings.languageRestartTitle,
            message = NovaStrings.strings.languageRestartMessage,
            confirmText = NovaStrings.strings.languageRestartNow,
            dismissText = NovaStrings.strings.languageRestartLater,
            onConfirm = {
                restartPrompt = false
                onLanguageNeedsRestart()
            },
            onDismiss = { restartPrompt = false },
        )
    }

    if (themeDialog) {
        OptionPickerDialog(
            title = NovaStrings.strings.appearance,
            options = AppThemeMode.entries.map { NovaStrings.strings.themeLabel(it) },
            selectedIndex = AppThemeMode.entries.indexOf(themeMode),
            onConfirm = { index ->
                onThemeChange(AppThemeMode.entries[index])
                themeDialog = false
            },
            onDismiss = { themeDialog = false },
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NovaTheme.colors.card,
    ) {
        Column(modifier = Modifier.padding(vertical = 2.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(21.dp),
                tint = NovaTheme.colors.textPrimary,
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = NovaTheme.colors.textPrimary,
            )
        }
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTheme.colors.textSecondary,
                modifier = Modifier.padding(start = 12.dp),
            )
            Spacer(Modifier.width(6.dp))
        } else {
            Spacer(Modifier.width(12.dp))
        }
        Icon(
            imageVector = NovaIcons.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(17.dp),
            tint = NovaTheme.colors.textTertiary,
        )
    }
}
