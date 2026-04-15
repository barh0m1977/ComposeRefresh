package com.ibrahim.composescrollrefresh

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * A state object that can be hoisted to control and observe the pull-to-refresh state.
 *
 * @param refreshThreshold The distance in pixels that the user must pull to trigger a refresh.
 * @param refreshingThreshold The distance in pixels where the indicator rests while refreshing.
 */
@Stable
class RefreshScrollState(
    val refreshThreshold: Float,
    val refreshingThreshold: Float,
    private val animationsEnabled: Boolean = true
) {
    /**
     * The current distance pulled in pixels.
     */
    private val _pullDistance = Animatable(0f, Float.VectorConverter)
    val pullDistance: Float get() = _pullDistance.value

    /**
     * Whether a refresh is currently in progress.
     */
    var isRefreshing by mutableStateOf(false)

    /**
     * The progress of the pull gesture, from 0.0 to 1.0 (or more if pulled beyond threshold).
     */
    val progress by derivedStateOf {
        if (refreshThreshold <= 0f) 0f else (pullDistance / refreshThreshold).coerceAtLeast(0f)
    }

    /**
     * Internal state to track if we are currently dragging.
     */
    internal var isDragging by mutableStateOf(false)

    /**
     * Dispatches a pull delta to the state.
     *
     * @param delta The delta to pull, in pixels.
     * @return The amount of delta consumed.
     */
    internal suspend fun onPull(delta: Float): Float {
        if (isRefreshing) return 0f

        // Apply some resistance/friction to the pull
        val resistance = 0.5f
        val newDistance = (pullDistance + delta * resistance).coerceAtLeast(0f)
        val consumed = (newDistance - pullDistance) / resistance
        _pullDistance.snapTo(newDistance)
        return consumed
    }

    /**
     * Notifies the state that a pull gesture has finished.
     */
    internal suspend fun onRelease(onRefresh: () -> Unit) {
        if (isRefreshing) return

        if (pullDistance >= refreshThreshold) {
            isRefreshing = true
            animateToRefreshing()
            onRefresh()
        } else {
            animateToZero()
        }
    }

    /**
     * Animates the pull distance to the refreshing threshold.
     */
    internal suspend fun animateToRefreshing() {
        if (animationsEnabled) {
            _pullDistance.animateTo(refreshingThreshold)
        } else {
            _pullDistance.snapTo(refreshingThreshold)
        }
    }

    /**
     * Animates the pull distance back to zero.
     */
    internal suspend fun animateToZero() {
        if (animationsEnabled) {
            _pullDistance.animateTo(0f)
        } else {
            _pullDistance.snapTo(0f)
        }    }

    /**
     * Resets the state after a refresh has finished.
     */
    suspend fun endRefresh() {
        isRefreshing = false
        animateToZero()
    }
}

/**
 * Creates a [RefreshScrollState] that is remembered across compositions.
 *
 * @param refreshThreshold The distance in pixels that the user must pull to trigger a refresh.
 * @param refreshingThreshold The distance in pixels where the indicator rests while refreshing.
 */
@Composable
fun rememberRefreshScrollState(
    refreshThreshold: Float,
    refreshingThreshold: Float = refreshThreshold
): RefreshScrollState {
    return remember(refreshThreshold, refreshingThreshold) {
        RefreshScrollState(refreshThreshold, refreshingThreshold)
    }
}
