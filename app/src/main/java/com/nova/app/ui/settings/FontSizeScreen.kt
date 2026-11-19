package com.nova.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 字号档位：0.85 到 1.30，步进 0.05，共 10 档。 */
val FontScaleSteps: List<Float> = listOf(
    0.85f, 0.90f, 0.95f, 1.00f, 1.05f,
    1.10f, 1.15f, 1.20f, 1.25f, 1.30f,
)

const val DefaultFontScaleIndex = 3

fun fontScaleLabel(index: Int): String = when (index) {
    3 -> "标准"
    in 0..2 -> "较小"
    else -> "较大"
}

@Composable
fun FontSizeScreen(
    scaleIndex: Int,
    followSystem: Boolean,
    onScaleChange: (Int) -> Unit,
    onFollowSystemChange: (Boolean) -> Unit,
) {
    val scale = FontScaleSteps[scaleIndex.coerceIn(FontScaleSteps.indices)]

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                ) {
                    Text(
                        text = "预览文字大小",
                        fontSize = (16 * scale).sp,
                        lineHeight = (22 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
            Spacer(Modifier.height(26.dp))
            Text(
                text = "拖动下方滑块就能调整全局字号，所见即所得。",
                fontSize = (16 * scale).sp,
                lineHeight = (24 * scale).sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "长段代码看久了容易累，把字号调到自己舒服的档位，界面其它地方也会一起变。",
                fontSize = (16 * scale).sp,
                lineHeight = (24 * scale).sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "跟随系统",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Switch(
                        checked = followSystem,
                        onCheckedChange = onFollowSystemChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF34C759),
                            checkedBorderColor = Color.Transparent,
                        ),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "A",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = fontScaleLabel(scaleIndex),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Text(
                        text = "A",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                val sliderState = rememberSliderState(
                    value = scaleIndex.toFloat(),
                    steps = FontScaleSteps.size - 2,
                    trackRange = 0f..(FontScaleSteps.size - 1).toFloat(),
                )
                LaunchedEffect(scaleIndex) {
                    sliderState.value = scaleIndex.toFloat()
                }
                Slider(
                    state = sliderState,
                    onValueChange = { if (!followSystem) onScaleChange(it.toInt()) },
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant,
                    ),
                )
            }
        }
    }
}


