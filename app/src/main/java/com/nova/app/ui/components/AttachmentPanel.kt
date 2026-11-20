package com.nova.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.rememberPressScale
import com.nova.app.ui.theme.NovaMotion
import com.nova.app.ui.theme.NovaTheme

@Composable
fun AttachmentPanel(
    recentPhotos: List<String>,
    selectedPhotos: Set<String>,
    loading: Boolean,
    onTogglePhoto: (String) -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        when {
            loading -> {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false,
                ) {
                    items(6) { PhotoSkeleton() }
                }
                Spacer(Modifier.height(14.dp))
            }
            recentPhotos.isNotEmpty() -> {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
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
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AttachmentAction(
                icon = NovaIcons.Camera,
                label = NovaStrings.strings.attachCamera,
                onClick = onCamera,
                modifier = Modifier.weight(1f),
            )
            AttachmentAction(
                icon = NovaIcons.Gallery,
                label = NovaStrings.strings.attachGallery,
                onClick = onGallery,
                modifier = Modifier.weight(1f),
            )
            AttachmentAction(
                icon = NovaIcons.File,
                label = NovaStrings.strings.attachFile,
                onClick = onFile,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PhotoSkeleton() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = NovaMotion.ShimmerMin,
        targetValue = NovaMotion.ShimmerMax,
        animationSpec = infiniteRepeatable(
            animation = tween(NovaMotion.Shimmer, easing = NovaMotion.EasingLinear),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmerAlpha",
    )
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(NovaTheme.colors.containerHigh.copy(alpha = alpha)),
    )
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
            color = NovaTheme.colors.containerLow,
            border = if (selected) {
                androidx.compose.foundation.BorderStroke(2.dp, NovaTheme.colors.accent)
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
                            color = NovaTheme.colors.accent,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = NovaIcons.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(11.dp),
                                    tint = NovaTheme.colors.onAccent,
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
    val scale by rememberPressScale(pressed)
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = onClick,
            modifier = modifier
                .height(76.dp)
                .scale(scale),
            shape = RoundedCornerShape(16.dp),
            color = NovaTheme.colors.containerHigh,
            border = androidx.compose.foundation.BorderStroke(
                0.8.dp,
                NovaTheme.colors.border,
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
                    tint = NovaTheme.colors.textPrimary,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTheme.colors.textPrimary,
                )
            }
        }
    }
}
