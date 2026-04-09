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
 * SwipeRefresh(style = RefreshIndicatorRegistry.wave.classicWave) { ... }
 * ```
 *
 * ── Wave ──────────────────────────────────────────────
 *  [wave.classicWave]        Classic rolling wave
 *  [wave.doubleWave]         Two stacked waves
 *  [wave.strokeWave]         Outlined/stroke wave
 *  [wave.zigzagWave]         Zigzag line wave
 *  [wave.rippleWave]         Expanding ripple rings
 *  [wave.squareWave]         Square/pulse wave
 *  [wave.bouncingDotsWave]   Row of bouncing dots
 *  [wave.layeredWave]        Multiple layered waves
 *  [wave.cosineWave]         Smooth cosine curve
 *  [wave.reverseWave]        Wave that plays in reverse
 *
 * ── Bubble ────────────────────────────────────────────
 *  [bubble.classicBubble]    Single rising bubble
 *  [bubble.gridBubble]       Grid of pulsing bubbles
 *  [bubble.ringsBubble]      Expanding concentric rings
 *  [bubble.confettiBubble]   Colorful confetti burst
 *  [bubble.pulseBubble]      Single pulsing bubble
 *  [bubble.clusterBubble]    Cluster of bubbles
 *  [bubble.lavaLampBubble]   Lava-lamp blob motion
 *  [bubble.snowfallBubble]   Falling snowflakes
 *  [bubble.spiralBubble]     Spiral particle trail
 *  [bubble.heartbeatBubble]  Heartbeat pulse effect
 *
 * ── Spring ────────────────────────────────────────────
 *  [spring.classicSpring]       Classic coil spring
 *  [spring.coilSpring]          Tight coil animation
 *  [spring.bouncingBallSpring]  Ball bouncing on spring
 *  [spring.elasticBarSpring]    Elastic bar stretch
 *  [spring.trampolineSpring]    Trampoline bounce
 *  [spring.starBurstSpring]     Star burst on release
 *  [spring.concentricSpring]    Concentric ring spring
 *  [spring.jellySpring]         Jelly wobble
 *  [spring.pendulumSpring]      Swinging pendulum
 *  [spring.rubberBandSpring]    Rubber band snap
 *
 * ── Advanced ──────────────────────────────────────────
 *  [advanced.classicAdvanced]    Spinning arc (Material-like)
 *  [advanced.dualArcAdvanced]    Two counter-rotating arcs
 *  [advanced.dotsCircleAdvanced] Dots arranged in a circle
 *  [advanced.clockAdvanced]      Clock hand sweep
 *  [advanced.segmentsAdvanced]   Rotating dashed segments
 *  [advanced.radarAdvanced]      Radar sweep
 *  [advanced.growingArcAdvanced] Arc that grows then resets
 *  [advanced.tripleArcAdvanced]  Three nested arcs
 *  [advanced.orbitAdvanced]      Orbiting dot
 *  [advanced.materialAdvanced]   Material Design spinner
 *
 * ── Fire ──────────────────────────────────────────────
 *  [fire.fireFlicker]        Flickering flame
 *
 * ─── HOW TO ADD A NEW STYLE ───────────────────────────
 *  Option A – existing category:
 *    1. Create MyNewWaveStyle.kt in styles/wave/
 *    2. Implement [RefreshIndicatorStyle]
 *    3. Add a val to the [wave] object below + one line to this KDoc.
 *
 *  Option B – new category:
 *    1. Create a new styles/mycat/ package
 *    2. Implement [RefreshIndicatorStyle] classes
 *    3. Add a new inner object below + a new section to this KDoc.
 * ──────────────────────────────────────────────────────
 */
object RefreshIndicatorRegistry {

    object wave {
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

    object bubble {
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

    object spring {
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

    object advanced {
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

    object fire {
        val fireFlicker = FireFlickerStyle()
    }

    // ── Legacy list API (kept for compatibility) ───────
    data class Category(val name: String, val styles: List<RefreshIndicatorStyle>)

    val categories: List<Category> = listOf(
        Category("Wave",     wave.run     { listOf(classicWave, doubleWave, strokeWave, zigzagWave, rippleWave, squareWave, bouncingDotsWave, layeredWave, cosineWave, reverseWave) }),
        Category("Bubble",   bubble.run   { listOf(classicBubble, gridBubble, ringsBubble, confettiBubble, pulseBubble, clusterBubble, lavaLampBubble, snowfallBubble, spiralBubble, heartbeatBubble) }),
        Category("Spring",   spring.run   { listOf(classicSpring, coilSpring, bouncingBallSpring, elasticBarSpring, trampolineSpring, starBurstSpring, concentricSpring, jellySpring, pendulumSpring, rubberBandSpring) }),
        Category("Advanced", advanced.run { listOf(classicAdvanced, dualArcAdvanced, dotsCircleAdvanced, clockAdvanced, segmentsAdvanced, radarAdvanced, growingArcAdvanced, tripleArcAdvanced, orbitAdvanced, materialAdvanced) }),
        Category("Fire",     fire.run     { listOf(fireFlicker) }),
    )
}