package com.ibrahim.composescrollrefresh

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A modifier that adds pull-to-refresh functionality to a scrollable container.
 *
 * @param state The [RefreshScrollState] to use.
 * @param onRefresh The callback to be invoked when a refresh is triggered.
 * @param enabled Whether the pull-to-refresh is enabled.
 */
@Composable
fun Modifier.pullToRefresh(
    state: RefreshScrollState,
    enabled: Boolean = true,
    onRefresh: () -> Unit
): Modifier {
    val scope = rememberCoroutineScope()
    val connection = remember(state, enabled, onRefresh, scope) {
        PullToRefreshNestedScrollConnection(state, enabled, onRefresh, scope)
    }
    return this.nestedScroll(connection)
}

private class PullToRefreshNestedScrollConnection(
    private val state: RefreshScrollState,
    private val enabled: Boolean,
    private val onRefresh: () -> Unit,
    private val scope: CoroutineScope
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (!enabled || state.isRefreshing) return Offset.Zero

        // If we are pushing up (available.y < 0) and we have some pull distance to consume.
        return if (source == NestedScrollSource.UserInput && available.y < 0 && state.pullDistance > 0f) {
            val consumed = available.y.coerceAtLeast(-state.pullDistance)
            scope.launch {
                state.onPull(consumed)
            }
            Offset(0f, consumed)
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (!enabled || state.isRefreshing) return Offset.Zero

        // If we are pulling down (available.y > 0)
        return if (source == NestedScrollSource.UserInput && available.y > 0) {
            scope.launch {
                state.onPull(available.y)
            }
            Offset(0f, available.y)
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
        if (state.pullDistance > 0f && !state.isRefreshing) {
            state.onRelease(onRefresh)
            // Consume all fling if we are currently pulled? 
            // Or maybe just let it be. Usually, we want to stop the fling if we trigger a refresh or reset.
            return available
        }
        return super.onPreFling(available)
    }

    override suspend fun onPostFling(
        consumed: androidx.compose.ui.unit.Velocity,
        available: androidx.compose.ui.unit.Velocity
    ): androidx.compose.ui.unit.Velocity {
        if (state.pullDistance > 0f && !state.isRefreshing) {
            state.onRelease(onRefresh)
        }
        return super.onPostFling(consumed, available)
    }
}
