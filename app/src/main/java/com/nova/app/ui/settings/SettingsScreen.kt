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
                            text = "确认",
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
    onThemeChange: (AppThemeMode) -> Unit,
    onFontSize: () -> Unit,
    onPersonalize: () -> Unit,
    onAccount: () -> Unit,
    onDataManagement: () -> Unit,
    onCheckUpdate: () -> Unit,
    onOpenLogs: () -> Unit,
) {
    var languageDialog by remember { mutableStateOf(false) }
    var themeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.size(6.dp))
        Text(
            text = "账户",
            style = MaterialTheme.typography.labelLarge,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(start = 6.dp, top = 12.dp, bottom = 8.dp),
        )
        SettingsCard {
            SettingsRow(
                icon = NovaIcons.User,
                title = "助手预设",
                onClick = onAccount,
            )
            SettingsRow(
                icon = NovaIcons.Database,
                title = "数据管理",
                onClick = onDataManagement,
            )
        }

        Text(
            text = "应用",
            style = MaterialTheme.typography.labelLarge,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(start = 6.dp, top = 22.dp, bottom = 8.dp),
        )
        SettingsCard {
            SettingsRow(
                icon = NovaIcons.Globe,
                title = "语言",
                value = language.label,
                onClick = { languageDialog = true },
            )
            SettingsRow(
                icon = NovaIcons.Sun,
                title = "外观",
                value = themeMode.label,
                onClick = { themeDialog = true },
            )
            SettingsRow(
                icon = NovaIcons.FontSize,
                title = "字体大小",
                onClick = onFontSize,
            )
            SettingsRow(
                icon = NovaIcons.Sparkle,
                title = "个性化",
                onClick = onPersonalize,
            )
        }

        Text(
            text = "关于",
            style = MaterialTheme.typography.labelLarge,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(start = 6.dp, top = 22.dp, bottom = 8.dp),
        )
        SettingsCard {
            SettingsRow(
                icon = NovaIcons.Info,
                title = "检查更新",
                value = versionName,
                onClick = onCheckUpdate,
            )
            SettingsRow(
                icon = NovaIcons.Document,
                title = "运行日志",
                onClick = onOpenLogs,
            )
        }

        Text(
            text = "内容由 AI 生成，请仔细甄别，并合法使用",
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
            title = "语言",
            options = AppLanguage.entries.map { it.label },
            selectedIndex = AppLanguage.entries.indexOf(language),
            onConfirm = { index ->
                onLanguageChange(AppLanguage.entries[index])
                languageDialog = false
            },
            onDismiss = { languageDialog = false },
        )
    }

    if (themeDialog) {
        OptionPickerDialog(
            title = "外观",
            options = AppThemeMode.entries.map { it.label },
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
