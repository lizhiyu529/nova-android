package com.nova.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun AttachmentPanel(
    recentPhotos: List<String>,
    selectedPhotos: Set<String>,
    onTogglePhoto: (String) -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (recentPhotos.isNotEmpty()) {
            Text(
                text = "最近照片",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 18.dp, top = 10.dp, bottom = 8.dp),
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(recentPhotos) { uri ->
                    PhotoThumb(
                        uri = uri,
                        selected = uri in selectedPhotos,
                        onClick = { onTogglePhoto(uri) },
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AttachmentAction(
                icon = NovaIcons.Camera,
                label = "拍照",
                onClick = onCamera,
                modifier = Modifier.weight(1f),
            )
            AttachmentAction(
                icon = NovaIcons.Gallery,
                label = "相册",
                onClick = onGallery,
                modifier = Modifier.weight(1f),
            )
            AttachmentAction(
                icon = NovaIcons.File,
                label = "文件",
                onClick = onFile,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PhotoThumb(
    uri: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = if (selected) {
                androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            } else {
                null
            },
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(uri).crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp)),
                )
                if (selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(18.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
                            modifier = Modifier.size(18.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = NovaIcons.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(11.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 320f),
        label = "attachmentScale",
    )
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = onClick,
            modifier = modifier
                .height(76.dp)
                .scale(scale),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = androidx.compose.foundation.BorderStroke(
                0.8.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
            ),
            interactionSource = interactionSource,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
