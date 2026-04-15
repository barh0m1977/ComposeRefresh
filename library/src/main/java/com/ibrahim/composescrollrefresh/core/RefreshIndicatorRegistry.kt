package com.ibrahim.composescrollrefresh.core

import com.ibrahim.composescrollrefresh.styles.advanced.*
import com.ibrahim.composescrollrefresh.styles.bubble.*
import com.ibrahim.composescrollrefresh.styles.fire.FireFlickerStyle
import com.ibrahim.composescrollrefresh.styles.spring.*
import com.ibrahim.composescrollrefresh.styles.wave.*

/**
 * Central registry of all available refresh indicator styles.
 *
 * Pass any style from this registry directly to your composable:
 * ```kotlin
 * SwipeRefresh(style = RefreshIndicatorRegistry.Wave.classicWave) { ... }
 * ```
 *
 * ── Wave ──────────────────────────────────────────────
 *  [Wave.classicWave]        Classic rolling Wave
 *  [Wave.doubleWave]         Two stacked waves
 *  [Wave.strokeWave]         Outlined/stroke Wave
 *  [Wave.zigzagWave]         Zigzag line Wave
 *  [Wave.rippleWave]         Expanding ripple rings
 *  [Wave.squareWave]         Square/pulse Wave
 *  [Wave.bouncingDotsWave]   Row of bouncing dots
 *  [Wave.layeredWave]        Multiple layered waves
 *  [Wave.cosineWave]         Smooth cosine curve
 *  [Wave.reverseWave]        Wave that plays in reverse
 *
 * ── Bubble ────────────────────────────────────────────
 *  [Bubble.classicBubble]    Single rising Bubble
 *  [Bubble.gridBubble]       Grid of pulsing bubbles
 *  [Bubble.ringsBubble]      Expanding concentric rings
 *  [Bubble.confettiBubble]   Colorful confetti burst
 *  [Bubble.pulseBubble]      Single pulsing Bubble
 *  [Bubble.clusterBubble]    Cluster of bubbles
 *  [Bubble.lavaLampBubble]   Lava-lamp blob motion
 *  [Bubble.snowfallBubble]   Falling snowflakes
 *  [Bubble.spiralBubble]     Spiral particle trail
 *  [Bubble.heartbeatBubble]  Heartbeat pulse effect
 *
 * ── Spring ────────────────────────────────────────────
 *  [Spring.classicSpring]       Classic coil Spring
 *  [Spring.coilSpring]          Tight coil animation
 *  [Spring.bouncingBallSpring]  Ball bouncing on Spring
 *  [Spring.elasticBarSpring]    Elastic bar stretch
 *  [Spring.trampolineSpring]    Trampoline bounce
 *  [Spring.starBurstSpring]     Star burst on release
 *  [Spring.concentricSpring]    Concentric ring Spring
 *  [Spring.jellySpring]         Jelly wobble
 *  [Spring.pendulumSpring]      Swinging pendulum
 *  [Spring.rubberBandSpring]    Rubber band snap
 *
 * ── Advanced ──────────────────────────────────────────
 *  [Advanced.classicAdvanced]    Spinning arc (Material-like)
 *  [Advanced.dualArcAdvanced]    Two counter-rotating arcs
 *  [Advanced.dotsCircleAdvanced] Dots arranged in a circle
 *  [Advanced.clockAdvanced]      Clock hand sweep
 *  [Advanced.segmentsAdvanced]   Rotating dashed segments
 *  [Advanced.radarAdvanced]      Radar sweep
 *  [Advanced.growingArcAdvanced] Arc that grows then resets
 *  [Advanced.tripleArcAdvanced]  Three nested arcs
 *  [Advanced.orbitAdvanced]      Orbiting dot
 *  [Advanced.materialAdvanced]   Material Design spinner
 *
 * ── Fire ──────────────────────────────────────────────
 *  [Fire.fireFlicker]        Flickering flame
 *
 * ─── HOW TO ADD A NEW STYLE ───────────────────────────
 *  Option A – existing category:
 *    1. Create MyNewWaveStyle.kt in styles/Wave/
 *    2. Implement [RefreshIndicatorStyle]
 *    3. Add a val to the [Wave] object below + one line to this KDoc.
 *
 *  Option B – new category:
 *    1. Create a new styles/mycat/ package
 *    2. Implement [RefreshIndicatorStyle] classes
 *    3. Add a new inner object below + a new section to this KDoc.
 * ──────────────────────────────────────────────────────
 */
object RefreshIndicatorRegistry {

    object Wave {
        val classicWave        = ClassicWaveStyle()
        val doubleWave         = DoubleWaveStyle()
        val strokeWave         = StrokeWaveStyle()
        val zigzagWave         = ZigzagWaveStyle()
        val rippleWave         = RippleWaveStyle()
        val squareWave         = SquareWaveStyle()
        val bouncingDotsWave   = BouncingDotsWaveStyle()
        val layeredWave        = LayeredWaveStyle()
        val cosineWave         = CosineWaveStyle()
        val reverseWave        = ReverseWaveStyle()
    }

    object Bubble {
        val classicBubble   = ClassicBubbleStyle()
        val gridBubble      = GridBubbleStyle()
        val ringsBubble     = RingsBubbleStyle()
        val confettiBubble  = ConfettiBubbleStyle()
        val pulseBubble     = PulseBubbleStyle()
        val clusterBubble   = ClusterBubbleStyle()
        val lavaLampBubble  = LavaLampBubbleStyle()
        val snowfallBubble  = SnowfallBubbleStyle()
        val spiralBubble    = SpiralBubbleStyle()
        val heartbeatBubble = HeartbeatBubbleStyle()
    }

    object Spring {
        val classicSpring       = ClassicSpringStyle()
        val coilSpring          = CoilSpringStyle()
        val bouncingBallSpring  = BouncingBallSpringStyle()
        val elasticBarSpring    = ElasticBarSpringStyle()
        val trampolineSpring    = TrampolineSpringStyle()
        val starBurstSpring     = StarBurstSpringStyle()
        val concentricSpring    = ConcentricSpringStyle()
        val jellySpring         = JellySpringStyle()
        val pendulumSpring      = PendulumSpringStyle()
        val rubberBandSpring    = RubberBandSpringStyle()
    }

    object Advanced {
        val classicAdvanced     = ClassicAdvancedStyle()
        val dualArcAdvanced     = DualArcAdvancedStyle()
        val dotsCircleAdvanced  = DotsCircleAdvancedStyle()
        val clockAdvanced       = ClockAdvancedStyle()
        val segmentsAdvanced    = SegmentsAdvancedStyle()
        val radarAdvanced       = RadarAdvancedStyle()
        val growingArcAdvanced  = GrowingArcAdvancedStyle()
        val tripleArcAdvanced   = TripleArcAdvancedStyle()
        val orbitAdvanced       = OrbitAdvancedStyle()
        val materialAdvanced    = MaterialAdvancedStyle()
    }

    object Fire {
        val fireFlicker = FireFlickerStyle()
    }

    // ── Legacy list API (kept for compatibility) ───────
    data class Category(val name: String, val styles: List<RefreshIndicatorStyle>)

    val categories: List<Category> = listOf(
        Category("Wave",     Wave.run     { listOf(classicWave, doubleWave, strokeWave, zigzagWave, rippleWave, squareWave, bouncingDotsWave, layeredWave, cosineWave, reverseWave) }),
        Category("Bubble",   Bubble.run   { listOf(classicBubble, gridBubble, ringsBubble, confettiBubble, pulseBubble, clusterBubble, lavaLampBubble, snowfallBubble, spiralBubble, heartbeatBubble) }),
        Category("Spring",   Spring.run   { listOf(classicSpring, coilSpring, bouncingBallSpring, elasticBarSpring, trampolineSpring, starBurstSpring, concentricSpring, jellySpring, pendulumSpring, rubberBandSpring) }),
        Category("Advanced", Advanced.run { listOf(classicAdvanced, dualArcAdvanced, dotsCircleAdvanced, clockAdvanced, segmentsAdvanced, radarAdvanced, growingArcAdvanced, tripleArcAdvanced, orbitAdvanced, materialAdvanced) }),
        Category("Fire",     Fire.run     { listOf(fireFlicker) }),
    )
}