package com.ibrahim.composescrollrefresh.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlin.math.PI

/**
 * Produces an [IndicatorAnimState] that contains only the animation values
 * requested by [requiredSlots].  Unrequested slots default to 0f and their
 * underlying [animateFloat] calls are never made — keeping composition cheap.
 */
@Composable
fun rememberIndicatorAnimState(requiredSlots: Set<AnimSlot>): IndicatorAnimState {
    val transition = rememberInfiniteTransition(label = "indicator")

    @Composable
    fun slot(slot: AnimSlot, from: Float, to: Float, durationMs: Int): Float {
        if (slot !in requiredSlots) return 0f
        val value by transition.animateFloat(
            initialValue = from,
            targetValue = to,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = slot.name
        )
        return value
    }

    val twoPI = (2f * PI).toFloat()

    return IndicatorAnimState(
        phase1000    = slot(AnimSlot.PHASE_1000,    0f,    twoPI, 1000),
        phase1400    = slot(AnimSlot.PHASE_1400,    0f,    twoPI, 1400),
        phase1800    = slot(AnimSlot.PHASE_1800,    0f,    twoPI, 1800),
        rotation1000 = slot(AnimSlot.ROTATION_1000, 0f,    360f,  1000),
        rotation1400 = slot(AnimSlot.ROTATION_1400, 360f,  0f,    1400),
        rotation700  = slot(AnimSlot.ROTATION_700,  0f,    360f,  700),
        pulse1200    = slot(AnimSlot.PULSE_1200,    0f,    1f,    1200),
        float2000    = slot(AnimSlot.FLOAT_2000,    0f,    100f,  2000),
        float3000    = slot(AnimSlot.FLOAT_3000,    0f,    1f,    3000),
    )
}