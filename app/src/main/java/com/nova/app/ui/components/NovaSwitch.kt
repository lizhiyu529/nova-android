package com.nova.app.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import com.nova.app.ui.theme.NovaTheme

@Composable
fun NovaSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = NovaTheme.colors.controlThumb,
            checkedTrackColor = NovaTheme.colors.controlActive,
            checkedBorderColor = NovaTheme.colors.controlActive,
            uncheckedThumbColor = NovaTheme.colors.controlInactiveThumb,
            uncheckedTrackColor = NovaTheme.colors.controlInactiveTrack,
            uncheckedBorderColor = NovaTheme.colors.controlInactiveTrack,
        ),
    )
}
