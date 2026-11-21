package com.nova.app.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun AutoScrollToBottom(
    listState: LazyListState,
    itemCount: Int,
    contentSignature: Int,
) {
    val count by rememberUpdatedState(itemCount)
    val signature by rememberUpdatedState(contentSignature)

    LaunchedEffect(listState) {
        snapshotFlow { count to signature }
            .distinctUntilChanged()
            .filter { it.first > 0 }
            .collect {
                val target = count - 1
                if (target >= 0) {
                    listState.scrollToItem(target)
                }
            }
    }
}
