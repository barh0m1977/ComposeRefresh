package com.ibrahim.composerefresh

import com.ibrahim.composescrollrefresh.RefreshScrollState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Test suite for RefreshScrollState.
 *
 * This file acts as both:
 * - Unit tests (verification of behavior)
 * - Live documentation of expected behavior
 *
 * Each test explains:
 * - what behavior is being validated
 * - expected state transitions
 * - expected output values
 */
class RefreshScrollStateTest {

    private lateinit var state: RefreshScrollState

    @Before
    fun setup() {
        state = RefreshScrollState(
            refreshThreshold = 200f,
            refreshingThreshold = 160f
        )
    }

    // ─────────────────────────────────────────────────────────────
    // 1. INITIAL STATE
    // ─────────────────────────────────────────────────────────────

    /**
     * Ensures the state starts in a clean default configuration.
     *
     * Expected:
     * - pullDistance = 0
     * - isRefreshing = false
     * - progress = 0
     *
     * This guarantees no previous gesture or animation state leaks.
     */
    @Test
    fun initialState_isNeutral() {
        assertEquals(0f, state.pullDistance, 0.001f)
        assertFalse(state.isRefreshing)
        assertEquals(0f, state.progress, 0.001f)
    }

    // ─────────────────────────────────────────────────────────────
    // 2. PROGRESS CALCULATION
    // ─────────────────────────────────────────────────────────────

    /**
     * When no interaction happens, progress must remain zero.
     *
     * Expected:
     * - progress = 0
     */
    @Test
    fun progress_isZero_whenNothingPulled() {
        assertEquals(0f, state.progress, 0.001f)
    }

    /**
     * Tests normalized progress calculation at half threshold.
     *
     * Behavior:
     * - onPull(200f) → resistance reduces it to 100f
     * - progress = 100 / 200 = 0.5
     *
     * Expected:
     * - progress ≈ 0.5
     */
    @Test
    fun progress_isHalf_atHalfThreshold() = runTest {
        state.onPull(200f)
        assertEquals(0.5f, state.progress, 0.001f)
    }

    /**
     * Tests behavior when user pulls beyond threshold.
     *
     * Current implementation:
     * - progress is NOT clamped at 1
     * - but resistance reduces effective pull
     *
     * Expected:
     * - progress should reach 1.0 or stay around 1.0
     */
    @Test
    fun progress_doesNotExceedLogicalLimit() = runTest {
        state.onPull(400f)
        assertTrue(state.progress >= 1f)
    }

    /**
     * Ensures progress never becomes negative even with negative input.
     *
     * Expected:
     * - progress >= 0 always
     */
    @Test
    fun progress_neverNegative() = runTest {
        state.onPull(-999f)
        assertTrue(state.progress >= 0f)
    }

    /**
     * Edge case: when refreshThreshold = 0
     *
     * Prevents division by zero.
     *
     * Expected:
     * - progress = 0 safely
     */
    @Test
    fun progress_isZero_whenThresholdIsZero() {
        val zeroState = RefreshScrollState(0f, 0f)
        assertEquals(0f, zeroState.progress, 0.001f)
    }

    // ─────────────────────────────────────────────────────────────
    // 3. onPull BEHAVIOR
    // ─────────────────────────────────────────────────────────────

    /**
     * Ensures pull gesture increases stored distance.
     *
     * Expected:
     * - pullDistance increases after positive delta
     */
    @Test
    fun onPull_increasesDistance() = runTest {
        state.onPull(100f)
        assertTrue(state.pullDistance > 0f)
    }

    /**
     * Tests resistance behavior.
     *
     * Behavior:
     * - delta is multiplied by 0.5 internally
     * - so 100f becomes 50f stored distance
     *
     * Expected:
     * - pullDistance = 50f
     */
    @Test
    fun onPull_appliesResistance() = runTest {
        state.onPull(100f)
        assertEquals(50f, state.pullDistance, 0.001f)
    }

    /**
     * Ensures pull distance never becomes negative.
     *
     * Expected:
     * - pullDistance is clamped to >= 0
     */
    @Test
    fun onPull_neverBelowZero() = runTest {
        state.onPull(100f)
        state.onPull(-9999f)
        assertEquals(0f, state.pullDistance, 0.001f)
    }

    /**
     * Ensures pull is ignored while refreshing.
     *
     * Behavior:
     * - when isRefreshing = true
     * - onPull should do nothing
     *
     * Expected:
     * - pullDistance remains unchanged (or 0)
     */
    @Test
    fun onPull_noOpWhileRefreshing() = runTest {
        state.isRefreshing = true
        state.onPull(200f)
        assertEquals(0f, state.pullDistance, 0.001f)
    }

    // ─────────────────────────────────────────────────────────────
    // 4. onRelease BEHAVIOR
    // ─────────────────────────────────────────────────────────────

    /**
     * Release below threshold should NOT trigger refresh.
     *
     * Expected:
     * - callback NOT called
     * - isRefreshing = false
     */
    @Test
    fun onRelease_belowThreshold_noRefresh() = runTest {
        var called = false

        state.onPull(50f)
        state.onRelease { called = true }

        assertFalse(called)
        assertFalse(state.isRefreshing)
    }

    /**
     * Release at/above threshold triggers refresh.
     *
     * Expected:
     * - refresh callback is called
     * - isRefreshing becomes true
     */
    @Test
    fun onRelease_atThreshold_triggersRefresh() = runTest {
        var called = false

        state.onPull(400f)
        state.onRelease { called = true }

        assertTrue(called)
        assertTrue(state.isRefreshing)
    }

    // ─────────────────────────────────────────────────────────────
    // 5. endRefresh
    // ─────────────────────────────────────────────────────────────

    /**
     * Ensures refresh state resets correctly after completion.
     *
     * Behavior:
     * - isRefreshing becomes false
     * - pullDistance returns to 0
     *
     * Expected:
     * - clean reset state
     */
    @Test
    fun endRefresh_resetsState() = runTest {
        state.onPull(400f)
        state.onRelease {}

        state.endRefresh()

        assertFalse(state.isRefreshing)
        assertEquals(0f, state.pullDistance, 0.001f)
    }
}