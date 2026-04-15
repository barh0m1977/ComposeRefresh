package com.ibrahim.composescrollrefresh.styles.bubble

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ibrahim.composescrollrefresh.RefreshScrollState
import com.ibrahim.composescrollrefresh.core.AnimSlot
import com.ibrahim.composescrollrefresh.core.IndicatorAnimState
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import com.ibrahim.composescrollrefresh.core.get
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ─── Shared seed data ────────────────────────────────────────────────────────

private data class Particle(val xRel: Float, val yRel: Float, val sizeRel: Float, val speed: Float)

private val classicParticles = List(15) {
    Particle(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 0.5f + 0.5f, Random.nextFloat() * 0.5f + 0.5f)
}
private val lavaParticles = List(6) {
    Particle(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 0.5f + 1f, Random.nextFloat() * 0.3f + 0.2f)
}

private fun DrawScope.color() = androidx.compose.ui.graphics.Color(0xFF625B71)

// ─── 1. Classic ──────────────────────────────────────────────────────────────

class ClassicBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Classic"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        classicParticles.forEach { p ->
            val x = p.xRel * size.width
            val r = 10.dp.toPx() * p.sizeRel * progress
            val yOff = if (refreshState.isRefreshing) (animState[AnimSlot.FLOAT_2000] * p.speed % size.height) else 0f
            val y = (p.yRel * size.height - yOff + size.height) % size.height
            drawCircle(color().copy(alpha = 0.6f * progress), r, Offset(x, y))
        }
    }
}

// ─── 2. Grid pop ─────────────────────────────────────────────────────────────

class GridBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Grid"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val cols = 8; val rows = 4
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val cw = size.width / cols; val ch = size.height / rows
        for (row in 0 until rows) for (col in 0 until cols) {
            val idx = row * cols + col; val total = rows * cols
            val pop = ((t * total - idx + total) % total) / total.toFloat()
            val r = (6.dp.toPx() * progress * (0.5f + 0.5f * sin(pop * 2 * PI).toFloat())).coerceAtLeast(0f)
            drawCircle(color().copy(alpha = 0.5f * progress), r, Offset(cw * col + cw / 2, ch * row + ch / 2))
        }
    }
}

// ─── 3. Expanding rings ──────────────────────────────────────────────────────

class RingsBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Rings"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val center = Offset(size.width / 2, size.height / 2)
        val maxR = (size.width / 2).coerceAtMost(size.height) * 0.8f * progress
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        repeat(5) { i ->
            val frac = ((t + i / 5f) % 1f)
            drawCircle(color().copy(alpha = (1f - frac) * 0.7f * progress), frac * maxR, center, style = Stroke(3.dp.toPx()))
        }
    }
}

// ─── 4. Confetti ─────────────────────────────────────────────────────────────

class ConfettiBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Confetti"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000, AnimSlot.FLOAT_3000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val t2 = animState[AnimSlot.FLOAT_3000]
        val phase = animState[AnimSlot.PHASE_1000]
        repeat(30) { i ->
            val x = (i.toFloat() / 30 + 0.1f * sin(i * 1.7f + phase)) * size.width
            val y = ((t2 * 1.5f + i / 30f) % 1f) * size.height
            drawCircle(color().copy(alpha = 0.7f * progress), 3.dp.toPx() * progress, Offset(x % size.width, y))
        }
    }
}

// ─── 5. Pulse ────────────────────────────────────────────────────────────────

class PulseBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Pulse"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val center = Offset(size.width / 2, size.height / 2)
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val r = (20.dp.toPx() + 10.dp.toPx() * sin(t * 2 * PI).toFloat()) * progress
        drawCircle(color().copy(alpha = 0.8f * progress), r, center)
        drawCircle(color().copy(alpha = 0.3f * progress), r + 8.dp.toPx(), center, style = Stroke(2.dp.toPx()))
    }
}

// ─── 6. Orbit cluster ────────────────────────────────────────────────────────

class ClusterBubbleStyle : RefreshIndicatorStyle {
    override val key =" Bubble.Cluster"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val center = Offset(size.width / 2, size.height / 2)
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val orbitR = 25.dp.toPx() * progress
        repeat(8) { i ->
            val angle = (i / 8f) * 2 * PI.toFloat() + t * 2 * PI.toFloat()
            val r = (5.dp.toPx() * progress * (0.7f + 0.3f * sin(angle * 2))).coerceAtLeast(0f)
            drawCircle(color().copy(alpha = 0.7f * progress), r, Offset(center.x + cos(angle) * orbitR, center.y + sin(angle) * orbitR))
        }
        drawCircle(color().copy(alpha = 0.5f * progress), 7.dp.toPx() * progress, center)
    }
}

// ─── 7. Lava lamp ────────────────────────────────────────────────────────────

class LavaLampBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Lava"
    override val requiredAnims = setOf(AnimSlot.FLOAT_3000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val t = animState[AnimSlot.FLOAT_3000]
        lavaParticles.forEach { p ->
            val x = p.xRel * size.width
            val yOff = sin(t * 2 * PI.toFloat() * p.speed + p.yRel * 6f) * size.height * 0.3f
            val y = (p.yRel * size.height + yOff + size.height) % size.height
            drawCircle(color().copy(alpha = 0.4f * progress), 18.dp.toPx() * p.sizeRel * progress, Offset(x, y))
        }
    }
}

// ─── 8. Snowfall ─────────────────────────────────────────────────────────────

class SnowfallBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Snow"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000, AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val phase = animState[AnimSlot.PHASE_1000]
        classicParticles.forEach { p ->
            val x = p.xRel * size.width + 10.dp.toPx() * sin(t * 2 * PI.toFloat() * p.speed + p.xRel * 5f)
            val y = ((p.yRel + t * p.speed) % 1f) * size.height
            drawCircle(color().copy(alpha = 0.6f * progress), 5.dp.toPx() * p.sizeRel * progress, Offset(x, y))
        }
    }
}

// ─── 9. Spiral ───────────────────────────────────────────────────────────────

class SpiralBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Spiral"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val center = Offset(size.width / 2, size.height / 2)
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val maxR = 30.dp.toPx() * progress
        repeat(20) { i ->
            val angle = (i / 20f) * 4 * PI.toFloat() + t * 2 * PI.toFloat()
            val r = maxR * (i / 20f)
            val dot = 3.dp.toPx() * progress * (1f - i / 20f * 0.5f)
            drawCircle(color().copy(alpha = 0.7f * progress), dot, Offset(center.x + cos(angle) * r, center.y + sin(angle) * r))
        }
    }
}

// ─── 10. Heartbeat ───────────────────────────────────────────────────────────

class HeartbeatBubbleStyle : RefreshIndicatorStyle {
    override val key = "Bubble.Heartbeat"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val center = Offset(size.width / 2, size.height / 2)
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val beat = abs(sin(t * 4 * PI.toFloat()))
        repeat(5) { i ->
            val r = (6.dp.toPx() + 15.dp.toPx() * beat * (1f - i / 5f)) * progress
            val alpha = (1f - i / 5f) * beat * progress
            if (r > 0f) drawCircle(color().copy(alpha = alpha), r, center, style = if (i == 0) androidx.compose.ui.graphics.drawscope.Fill else Stroke(2.dp.toPx()))
        }
    }
}