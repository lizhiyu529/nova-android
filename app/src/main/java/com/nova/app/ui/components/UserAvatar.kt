package com.nova.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nova.app.ui.theme.NovaTheme
import java.io.File

@Composable
fun UserAvatar(
    avatarPath: String,
    size: Dp = 34.dp,
    modifier: Modifier = Modifier,
) {
    if (avatarPath.isNotBlank() && File(avatarPath).exists()) {
        AsyncImage(
            model = File(avatarPath),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
        )
    } else {
        Surface(
            modifier = modifier.size(size),
            shape = CircleShape,
            color = NovaTheme.colors.containerHighest,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = NovaIcons.Person,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.58f),
                    tint = NovaTheme.colors.textSecondary,
                )
            }
        }
    }
}
