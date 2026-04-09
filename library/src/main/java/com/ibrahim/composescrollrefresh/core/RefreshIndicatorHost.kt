package com.ibrahim.composescrollrefresh.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.ibrahim.composescrollrefresh.RefreshScrollState

/**
 * Single composable that hosts any [RefreshIndicatorStyle].
 *
 * Usage:
 * ```
 * RefreshIndicatorHost(
 *     state  = refreshState,
 *     style  = ClassicWaveStyle(),   // ← swap to any implementation
 * )
 * ```
 *
 * The host:
 *   • Converts pull distance to Dp height.
 *   • Builds only the animation values the style declared it needs.
 *   • Calls [RefreshIndicatorStyle.draw] inside a Canvas.
 */
@Composable
fun RefreshIndicatorHost(
    state: RefreshScrollState,
    style: RefreshIndicatorStyle,
    modifier: Modifier = Modifier,
) {
    val density  = LocalDensity.current
    val height   = with(density) { state.pullDistance.toDp() }
    val animState = rememberIndicatorAnimState(style.requiredAnims)

    Box(modifier = modifier.fillMaxWidth().height(height)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            with(style) { draw(state, animState) }
        }
    }
}