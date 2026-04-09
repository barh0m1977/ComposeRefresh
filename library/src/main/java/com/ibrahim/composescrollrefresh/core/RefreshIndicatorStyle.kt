package com.ibrahim.composescrollrefresh.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.ibrahim.composescrollrefresh.RefreshScrollState

/**
 * Strategy interface for a single refresh indicator visual.
 *
 * To add a brand-new style:
 *   1. Create a new file anywhere in the project.
 *   2. Implement this interface.
 *   3. Register it in [RefreshIndicatorRegistry].
 *     Zero changes to existing files.
 */
interface RefreshIndicatorStyle {
    /** Unique key shown in the UI chip / tab. */
    val key: String

    /**
     * Called once per frame inside a [Canvas] draw scope.
     * All animation state must be provided via [animState].
     */
    fun DrawScope.draw(
        refreshState: RefreshScrollState,
        animState: IndicatorAnimState,
    )

    /**
     * Override to declare which animation slots this style actually uses.
     * Unused slots are never created, keeping composition lightweight.
     */
    val requiredAnims: Set<AnimSlot> get() = AnimSlot.entries.toSet()
}

/** Which pre-baked animation values a style may consume. */
enum class AnimSlot {
    PHASE_1000,   // 0→2π  in 1 000 ms  (fast wave / rotation)
    PHASE_1400,   // 0→2π  in 1 400 ms
    PHASE_1800,   // 0→2π  in 1 800 ms
    ROTATION_1000,// 0→360 in 1 000 ms
    ROTATION_1400,// 360→0 in 1 400 ms  (counter-rotation)
    ROTATION_700, // 0→360 in   700 ms  (fast)
    PULSE_1200,   // 0→1   in 1 200 ms  (generic pulse)
    FLOAT_2000,   // 0→100 in 2 000 ms  (bubble float)
    FLOAT_3000,   // 0→1   in 3 000 ms  (slow float)
}

/**
 * Bag of pre-animated floats.  Styles read only what they declared in
 * [RefreshIndicatorStyle.requiredAnims]; unneeded fields are 0f.
 */
data class IndicatorAnimState(
    val phase1000: Float = 0f,
    val phase1400: Float = 0f,
    val phase1800: Float = 0f,
    val rotation1000: Float = 0f,
    val rotation1400: Float = 0f,
    val rotation700: Float = 0f,
    val pulse1200: Float = 0f,
    val float2000: Float = 0f,
    val float3000: Float = 0f,
)

/** Convenience extension so styles can write `animState[AnimSlot.PHASE_1000]`. */
operator fun IndicatorAnimState.get(slot: AnimSlot): Float = when (slot) {
    AnimSlot.PHASE_1000    -> phase1000
    AnimSlot.PHASE_1400    -> phase1400
    AnimSlot.PHASE_1800    -> phase1800
    AnimSlot.ROTATION_1000 -> rotation1000
    AnimSlot.ROTATION_1400 -> rotation1400
    AnimSlot.ROTATION_700  -> rotation700
    AnimSlot.PULSE_1200    -> pulse1200
    AnimSlot.FLOAT_2000    -> float2000
    AnimSlot.FLOAT_3000    -> float3000
}