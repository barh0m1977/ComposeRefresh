package com.ibrahim.composescrollrefresh.core

import com.ibrahim.composescrollrefresh.styles.advanced.*
import com.ibrahim.composescrollrefresh.styles.bubble.*
import com.ibrahim.composescrollrefresh.styles.fire.FireFlickerStyle
import com.ibrahim.composescrollrefresh.styles.spring.*
import com.ibrahim.composescrollrefresh.styles.wave.*

/**
 * Central registry of all available indicator styles, grouped into categories.
 *
 * ─── HOW TO ADD A NEW STYLE ────────────────────────────────────────────────
 *
 *  Option A – add to an existing category:
 *    1. Create MyNewWaveStyle.kt inside styles/wave/
 *    2. Implement RefreshIndicatorStyle
 *    3. Add MyNewWaveStyle() to the `wave` list below.
 *       → Done. No other file needs to change.
 *
 *  Option B – add a completely new category:
 *    1. Create styles/fire/ package
 *    2. Implement as many RefreshIndicatorStyle classes as you like
 *    3. Add a new entry to [categories] below.
 *       → Done. The UI picks it up automatically.
 *
 * ───────────────────────────────────────────────────────────────────────────
 */
object RefreshIndicatorRegistry {

    data class Category(
        val name: String,
        val styles: List<RefreshIndicatorStyle>,
    )

    val categories: List<Category> = listOf(
        Category(
            name = "Wave",
            styles = listOf(
                ClassicWaveStyle(),
                DoubleWaveStyle(),
                StrokeWaveStyle(),
                ZigzagWaveStyle(),
                RippleWaveStyle(),
                SquareWaveStyle(),
                BouncingDotsWaveStyle(),
                LayeredWaveStyle(),
                CosineWaveStyle(),
                ReverseWaveStyle(),
            )
        ),

        Category(
            name = "Bubble",
            styles = listOf(
                ClassicBubbleStyle(),
                GridBubbleStyle(),
                RingsBubbleStyle(),
                ConfettiBubbleStyle(),
                PulseBubbleStyle(),
                ClusterBubbleStyle(),
                LavaLampBubbleStyle(),
                SnowfallBubbleStyle(),
                SpiralBubbleStyle(),
                HeartbeatBubbleStyle(),
            )
        ),

        Category(
            name = "Spring",
            styles = listOf(
                ClassicSpringStyle(),
                CoilSpringStyle(),
                BouncingBallSpringStyle(),
                ElasticBarSpringStyle(),
                TrampolineSpringStyle(),
                StarBurstSpringStyle(),
                ConcentricSpringStyle(),
                JellySpringStyle(),
                PendulumSpringStyle(),
                RubberBandSpringStyle(),
            )
        ),

        Category(
            name = "Advanced",
            styles = listOf(
                ClassicAdvancedStyle(),
                DualArcAdvancedStyle(),
                DotsCircleAdvancedStyle(),
                ClockAdvancedStyle(),
                SegmentsAdvancedStyle(),
                RadarAdvancedStyle(),
                GrowingArcAdvancedStyle(),
                TripleArcAdvancedStyle(),
                OrbitAdvancedStyle(),
                MaterialAdvancedStyle(),
            )
        ),

        Category(
            name = "Fire",
            styles = listOf(
                FireFlickerStyle(),
                // more fire styles here ...
            )
        )

    )
}