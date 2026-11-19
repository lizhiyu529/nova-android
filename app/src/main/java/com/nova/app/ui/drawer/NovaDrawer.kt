package com.nova.app.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nova.app.ui.components.NovaIcons
import java.time.LocalDateTime

@Composable
fun NovaDrawer(
    conversations: List<Conversation>,
    now: LocalDateTime,
    accountEmail: String,
    onOpenConversation: (Conversation) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
) {
    var pinnedExpanded by remember { mutableStateOf(true) }
    var selecting by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(setOf<String>()) }

    val groups = groupConversations(conversations, now)
    val pinnedGroup = groups.firstOrNull { it.first == ConversationGroup.Pinned }
    val restGroups = groups.filterNot { it.first == ConversationGroup.Pinned }

    fun toggleSelection(id: String) {
        selected = if (id in selected) selected - id else selected + id
    }

    fun exitSelection() {
        selecting = false
        selected = emptySet()
    }

    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Spacer(Modifier.statusBarsPadding())
            if (selecting) {
                SelectionTopBar(
                    onClose = { exitSelection() },
                )
            } else {
                DrawerSearchField(
                    onClick = onOpenSearch,
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 4.dp),
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp),
            ) {
                pinnedGroup?.let { (_, items) ->
                    item(key = "pinned-header") {
                        PinnedHeader(
                            count = items.size,
                            expanded = pinnedExpanded,
                            showSelectAction = !selecting,
                            onToggle = { pinnedExpanded = !pinnedExpanded },
                            onStartSelection = { selecting = true },
                        )
                    }
                    if (pinnedExpanded) {
                        items(items, key = { it.id }) { conversation ->
                            ConversationRow(
                                conversation = conversation,
                                selecting = selecting,
                                selected = conversation.id in selected,
                                onClick = {
                                    if (selecting) {
                                        toggleSelection(conversation.id)
                                    } else {
                                        onOpenConversation(conversation)
                                    }
                                },
                            )
                        }
                    }
                }
                restGroups.forEach { (group, items) ->
                    item(key = "header-${group.name}") {
                        SectionLabel(group.label)
                    }
                    items(items, key = { it.id }) { conversation ->
                        ConversationRow(
                            conversation = conversation,
                            selecting = selecting,
                            selected = conversation.id in selected,
                            onClick = {
                                if (selecting) {
                                    toggleSelection(conversation.id)
                                } else {
                                    onOpenConversation(conversation)
                                }
                            },
                        )
                    }
                }
            }
            if (selecting) {
                SelectionActionBar(
                    count = selected.size,
                    onPin = { exitSelection() },
                    onDelete = { exitSelection() },
                )
            } else {
                DrawerAccountBar(
                    email = accountEmail,
                    onOpenSettings = onOpenSettings,
                )
            }
        }
    }
}

@Composable
private fun DrawerSearchField(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = NovaIcons.Search,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "搜索对话内容...",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 16.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun SelectionTopBar(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 14.dp, top = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "选择对话",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Surface(
                onClick = onClose,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = NovaIcons.Close,
                        contentDescription = "退出选择",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun PinnedHeader(
    count: Int,
    expanded: Boolean,
    showSelectAction: Boolean,
    onToggle: () -> Unit,
    onStartSelection: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onToggle)
                .padding(start = 10.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "置顶 $count 个对话",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
            Spacer(Modifier.width(5.dp))
            Icon(
                imageVector = NovaIcons.ChevronDown,
                contentDescription = if (expanded) "收起置顶" else "展开置顶",
                modifier = Modifier
                    .size(16.dp)
                    .rotate(if (expanded) 0f else -90f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }
        if (showSelectAction) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                IconButton(
                    onClick = onStartSelection,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = NovaIcons.SelectAll,
                        contentDescription = "多选对话",
                        modifier = Modifier.size(19.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier.padding(start = 10.dp, top = 16.dp, bottom = 6.dp),
    )
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    selecting: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .clip(shape)
            .then(
                if (selecting) {
                    Modifier.toggleable(
                        value = selected,
                        onValueChange = { onClick() },
                    )
                } else {
                    Modifier.clickable(onClick = onClick)
                },
            )
            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(
            visible = selecting,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
        ) {
            SelectionCircle(
                selected = selected,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
        Text(
            text = conversation.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SelectionCircle(selected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(20.dp),
            shape = CircleShape,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            border = if (selected) {
                null
            } else {
                BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
            },
        ) {
            if (selected) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = NovaIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionActionBar(
    count: Int,
    onPin: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SelectionAction(
                icon = NovaIcons.Pin,
                label = "置顶",
                enabled = count > 0,
                onClick = onPin,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(22.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            )
            SelectionAction(
                icon = NovaIcons.Trash,
                label = "删除",
                enabled = count > 0,
                onClick = onDelete,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SelectionAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(19.dp),
            tint = tint,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
        )
    }
}

@Composable
private fun DrawerAccountBar(
    email: String,
    onOpenSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onOpenSettings)
            .padding(start = 12.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = NovaIcons.Person,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.width(11.dp))
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(38.dp),
            ) {
                Icon(
                    imageVector = NovaIcons.More,
                    contentDescription = "设置",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
