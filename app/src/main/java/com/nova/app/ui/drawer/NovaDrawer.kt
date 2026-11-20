package com.nova.app.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.components.UserAvatar
import com.nova.app.ui.theme.NovaTheme
import com.nova.app.ui.theme.NovaTransitions
import java.time.LocalDateTime
import kotlinx.coroutines.delay

@Composable
fun NovaDrawer(
    conversations: List<Conversation>,
    now: LocalDateTime,
    nickname: String,
    avatarPath: String,
    onEditNickname: (String) -> Unit,
    onChangeAvatar: () -> Unit,
    onOpenConversation: (Conversation) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    onRename: (String, String) -> Unit,
    onTogglePin: (String) -> Unit,
    onDelete: (Set<String>) -> Unit,
    onPinSelected: (Set<String>) -> Unit,
) {
    var pinnedExpanded by remember { mutableStateOf(true) }
    var selecting by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var menuTarget by remember { mutableStateOf<Conversation?>(null) }
    var renameTarget by remember { mutableStateOf<Conversation?>(null) }
    var confirmDelete by remember { mutableStateOf<Set<String>?>(null) }
    var nicknameEditing by remember { mutableStateOf(false) }

    val groups = remember(conversations, now) { groupConversations(conversations, now) }
    val strings = NovaStrings.strings
    val pinnedGroup = groups.firstOrNull { it.first == ConversationGroup.Pinned }
    val restGroups = remember(groups) { groups.filterNot { it.first == ConversationGroup.Pinned } }

    fun toggleSelection(id: String) {
        selected = if (id in selected) selected - id else selected + id
    }

    fun exitSelection() {
        selecting = false
        selected = emptySet()
    }

    val windowWidth = LocalConfiguration.current.screenWidthDp
    val drawerWidth = minOf(300, (windowWidth * 0.78f).toInt()).dp

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(drawerWidth),
        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
        drawerContainerColor = NovaTheme.colors.containerLow,
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
                                onLongClick = { menuTarget = conversation },
                            )
                        }
                    }
                }
                restGroups.forEach { (group, items) ->
                    item(key = "header-${group.name}") {
                        SectionLabel(group.label(strings))
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
                            onLongClick = { menuTarget = conversation },
                        )
                    }
                }
                if (conversations.isEmpty()) {
                    item(key = "empty-state") {
                        EmptyDrawerState()
                    }
                }
            }
            if (selecting) {
                SelectionActionBar(
                    count = selected.size,
                    onPin = {
                        if (selected.isNotEmpty()) {
                            onPinSelected(selected)
                            exitSelection()
                        }
                    },
                    onDelete = {
                        if (selected.isNotEmpty()) confirmDelete = selected
                    },
                )
            } else {
                DrawerAccountBar(
                    nickname = nickname,
                    avatarPath = avatarPath,
                    onOpenSettings = onOpenSettings,
                    onEditNickname = { nicknameEditing = true },
                    onChangeAvatar = onChangeAvatar,
                )
            }
        }
    }

    if (nicknameEditing) {
        NicknameDialog(
            initial = nickname,
            onDismiss = { nicknameEditing = false },
            onConfirm = { value ->
                onEditNickname(value)
                nicknameEditing = false
            },
        )
    }

    menuTarget?.let { target ->
        ConversationMenu(
            pinned = target.pinned,
            onDismiss = { menuTarget = null },
            onRename = {
                menuTarget = null
                renameTarget = target
            },
            onTogglePin = {
                menuTarget = null
                onTogglePin(target.id)
            },
            onMultiSelect = {
                menuTarget = null
                selecting = true
                selected = setOf(target.id)
            },
            onDelete = {
                menuTarget = null
                confirmDelete = setOf(target.id)
            },
        )
    }

    renameTarget?.let { target ->
        RenameDialog(
            initial = target.title,
            onDismiss = { renameTarget = null },
            onConfirm = { newTitle ->
                onRename(target.id, newTitle)
                renameTarget = null
            },
        )
    }

    confirmDelete?.let { ids ->
        DeleteConfirmDialog(
            count = ids.size,
            onDismiss = { confirmDelete = null },
            onConfirm = {
                onDelete(ids)
                confirmDelete = null
                exitSelection()
            },
        )
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
        color = NovaTheme.colors.containerHigh,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = NovaIcons.Search,
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = NovaTheme.colors.textSecondary,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = NovaStrings.strings.drawerSearchPlaceholder,
                style = MaterialTheme.typography.bodyLarge,
                color = NovaTheme.colors.textSecondary,
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
            text = NovaStrings.strings.selectConversations,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = NovaTheme.colors.textPrimary,
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Surface(
                onClick = onClose,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = NovaTheme.colors.containerHigh,
                border = BorderStroke(1.dp, NovaTheme.colors.divider),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = NovaIcons.Close,
                        contentDescription = NovaStrings.strings.exitSelection,
                        modifier = Modifier.size(18.dp),
                        tint = NovaTheme.colors.textSecondary,
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
                text = NovaStrings.strings.pinnedCount(count),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                color = NovaTheme.colors.textSecondary,
            )
            Spacer(Modifier.width(5.dp))
            Icon(
                imageVector = NovaIcons.ChevronDown,
                contentDescription = if (expanded) NovaStrings.strings.collapsePinned else NovaStrings.strings.expandPinned,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(if (expanded) 0f else -90f),
                tint = NovaTheme.colors.textSecondary,
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
                        contentDescription = NovaStrings.strings.selectAll,
                        modifier = Modifier.size(19.dp),
                        tint = NovaTheme.colors.textSecondary,
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
        color = NovaTheme.colors.textSecondary,
        modifier = Modifier.padding(start = 10.dp, top = 16.dp, bottom = 6.dp),
    )
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    selecting: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
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
                    Modifier.combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                    )
                },
            )
            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(
            visible = selecting,
            enter = NovaTransitions.inlineEnter,
            exit = NovaTransitions.inlineExit,
        ) {
            SelectionCircle(
                selected = selected,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
        Text(
            text = conversation.title,
            style = MaterialTheme.typography.bodyLarge,
            color = NovaTheme.colors.textPrimary,
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
            color = if (selected) NovaTheme.colors.accent else NovaTheme.colors.card,
            border = if (selected) {
                null
            } else {
                BorderStroke(1.5.dp, NovaTheme.colors.divider)
            },
        ) {
            if (selected) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = NovaIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = NovaTheme.colors.onAccent,
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
        color = NovaTheme.colors.card,
        shadowElevation = 0.dp,
        border = BorderStroke(0.5.dp, NovaTheme.colors.divider),
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
                label = NovaStrings.strings.pin,
                enabled = count > 0,
                onClick = onPin,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(22.dp)
                    .background(NovaTheme.colors.divider),
            )
            SelectionAction(
                icon = NovaIcons.Trash,
                label = NovaStrings.strings.delete,
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
        NovaTheme.colors.textPrimary
    } else {
        NovaTheme.colors.textDisabled
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
    nickname: String,
    avatarPath: String,
    onOpenSettings: () -> Unit,
    onEditNickname: (String) -> Unit,
    onChangeAvatar: () -> Unit,
) {
    val strings = NovaStrings.strings
    val label = nickname.ifBlank { strings.profileDefaultNickname }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .padding(start = 8.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .combinedClickable(
                    onClick = onEditNickname,
                    onLongClick = onChangeAvatar,
                ),
            contentAlignment = Alignment.Center,
        ) {
            UserAvatar(avatarPath = avatarPath, size = 34.dp)
        }
        Spacer(Modifier.width(9.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .combinedClickable(
                    onClick = onEditNickname,
                    onLongClick = onChangeAvatar,
                )
                .padding(horizontal = 4.dp, vertical = 2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = strings.avatarHint,
                style = MaterialTheme.typography.labelSmall,
                color = NovaTheme.colors.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(38.dp),
            ) {
                Icon(
                    imageVector = NovaIcons.More,
                    contentDescription = NovaStrings.strings.settingsTitle,
                    modifier = Modifier.size(20.dp),
                    tint = NovaTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun ConversationMenu(
    pinned: Boolean,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onTogglePin: () -> Unit,
    onMultiSelect: () -> Unit,
    onDelete: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 68.dp),
            shape = RoundedCornerShape(18.dp),
            color = NovaTheme.colors.card,
            tonalElevation = 0.dp,
        ) {
            Column {
                MenuRow(
                    icon = NovaIcons.Edit,
                    label = NovaStrings.strings.rename,
                    tint = NovaTheme.colors.textPrimary,
                    onClick = onRename,
                )
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                MenuRow(
                    icon = NovaIcons.Pin,
                    label = if (pinned) NovaStrings.strings.unpin else NovaStrings.strings.pin,
                    tint = NovaTheme.colors.textPrimary,
                    onClick = onTogglePin,
                )
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                MenuRow(
                    icon = NovaIcons.SelectAll,
                    label = NovaStrings.strings.multiSelect,
                    tint = NovaTheme.colors.textPrimary,
                    onClick = onMultiSelect,
                )
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                MenuRow(
                    icon = NovaIcons.Trash,
                    label = "删除",
                    tint = NovaTheme.colors.danger,
                    onClick = onDelete,
                )
            }
        }
    }
}

@Composable
private fun MenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = tint,
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = tint,
        )
    }
}

@Composable
private fun RenameDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initial) }
    var selection by remember { mutableStateOf(TextRange(initial.length)) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(120)
        selection = TextRange(text.length)
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.dp),
            shape = RoundedCornerShape(26.dp),
            color = NovaTheme.colors.card,
            tonalElevation = 0.dp,
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = NovaStrings.strings.renameTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NovaTheme.colors.textPrimary,
                    )
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = NovaTheme.colors.containerHigh,
                ) {
                    BasicTextField(
                        value = TextFieldValue(text = text, selection = selection),
                        onValueChange = {
                            text = it.text
                            selection = it.selection
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = NovaTheme.colors.textPrimary,
                        ),
                        cursorBrush = SolidColor(NovaTheme.colors.accent),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val trimmed = text.trim()
                                if (trimmed.isNotEmpty()) {
                                    keyboard?.hide()
                                    onConfirm(trimmed)
                                }
                            },
                        ),
                    )
                }
                Spacer(Modifier.size(16.dp))
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    DialogTextAction(
                        text = NovaStrings.strings.cancel,
                        color = NovaTheme.colors.textSecondary,
                        onClick = {
                            keyboard?.hide()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .width(0.7.dp)
                            .height(52.dp)
                            .background(NovaTheme.colors.divider),
                    )
                    DialogTextAction(
                        text = NovaStrings.strings.confirm,
                        color = NovaTheme.colors.accent,
                        onClick = {
                            val trimmed = text.trim()
                            if (trimmed.isNotEmpty()) {
                                keyboard?.hide()
                                onConfirm(trimmed)
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun NicknameDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val strings = NovaStrings.strings
    var text by remember { mutableStateOf(initial) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(120)
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.dp),
            shape = RoundedCornerShape(26.dp),
            color = NovaTheme.colors.dialog,
            tonalElevation = 0.dp,
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = strings.nicknameTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NovaTheme.colors.textPrimary,
                    )
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = NovaTheme.colors.containerHigh,
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it.take(24) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = NovaTheme.colors.textPrimary,
                        ),
                        cursorBrush = SolidColor(NovaTheme.colors.accent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onConfirm(text) }),
                        decorationBox = { innerTextField ->
                            if (text.isEmpty()) {
                                Text(
                                    text = strings.nicknamePlaceholder,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NovaTheme.colors.textSecondary,
                                )
                            }
                            innerTextField()
                        },
                    )
                }
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    NicknameAction(
                        text = strings.cancel,
                        color = NovaTheme.colors.textSecondary,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .width(0.7.dp)
                            .height(48.dp)
                            .background(NovaTheme.colors.divider),
                    )
                    NicknameAction(
                        text = strings.confirm,
                        color = NovaTheme.colors.accent,
                        onClick = { onConfirm(text) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun NicknameAction(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        color = androidx.compose.ui.graphics.Color.Transparent,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = color,
            )
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    count: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.dp),
            shape = RoundedCornerShape(26.dp),
            color = NovaTheme.colors.card,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = NovaStrings.strings.deleteTitle(count),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NovaTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = NovaStrings.strings.deleteMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.size(22.dp))
                Surface(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    color = NovaTheme.colors.danger,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "删除",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = NovaTheme.colors.onDanger,
                        )
                    }
                }
                Spacer(Modifier.size(4.dp))
                Surface(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "取消",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = NovaTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogTextAction(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = color,
        )
    }
}

@Composable
private fun EmptyDrawerState() {
    val strings = NovaStrings.strings
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = NovaIcons.NewChat,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = NovaTheme.colors.textDisabled,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = strings.emptyConversations,
            style = MaterialTheme.typography.bodyLarge,
            color = NovaTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = strings.emptyConversationsHint,
            style = MaterialTheme.typography.bodySmall,
            color = NovaTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
        )
    }
}
