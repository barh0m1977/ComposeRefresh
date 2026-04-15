package com.ibrahim.composescrollrefresh.styles.wave

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.ibrahim.composescrollrefresh.RefreshScrollState
import com.ibrahim.composescrollrefresh.core.AnimSlot
import com.ibrahim.composescrollrefresh.core.IndicatorAnimState
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import com.ibrahim.composescrollrefresh.core.get
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import kotlin.math.cos
import kotlin.math.sin

// ─── Shared helpers ──────────────────────────────────────────────────────────

private fun DrawScope.sineFilledPath(
    baseLine: Float,
    canvasH: Float,
    canvasW: Float,
    amplitude: Float,
    phase: Double,
    step: Float = 8f,
) = Path().apply {
    moveTo(0f, canvasH); lineTo(0f, baseLine)
    var x = 0f
    while (x <= canvasW) {
        lineTo(x, (baseLine + sin(x / canvasW * 2 * Math.PI + phase) * amplitude).toFloat())
        x += step
    }
    lineTo(canvasW, canvasH); close()
}

private fun DrawScope.waveBase(state: RefreshScrollState): Triple<Float, Float, Float> {
    val progress = state.progress.coerceAtMost(1f)
    val amp = 20.dp.toPx() * progress
    return Triple(size.width, size.height, amp)
}

// ─── 1. Classic filled sine ───────────────────────────────────────────────────

class ClassicWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Classic"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, h, amp) = waveBase(refreshState)
        val baseline = h - amp
        clipRect { drawPath(sineFilledPath(baseline, h, w, amp, animState[AnimSlot.PHASE_1000].toDouble()), defaultColor().copy(alpha = 0.85f)) }
    }
}

// ─── 2. Double overlapping waves ─────────────────────────────────────────────

class DoubleWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Double"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, h, amp) = waveBase(refreshState)
        val baseline = h - amp
        val phase = animState[AnimSlot.PHASE_1000].toDouble()
        val color = defaultColor()
        clipRect {
            drawPath(sineFilledPath(baseline, h, w, amp, phase), color.copy(alpha = 0.5f))
            drawPath(sineFilledPath(baseline, h, w, amp, phase + Math.PI), color.copy(alpha = 0.5f))
        }
    }
}

// ─── 3. Stroke-only sine ─────────────────────────────────────────────────────

class StrokeWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Stroke"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, h, amp) = waveBase(refreshState)
        val phase = animState[AnimSlot.PHASE_1000].toDouble()
        val path = Path().apply {
            var x = 0f; moveTo(x, (h / 2 + sin(phase) * amp).toFloat())
            while (x <= w) { lineTo(x, (h / 2 + sin(x / w * 2 * Math.PI + phase) * amp).toFloat()); x += 8f }
        }
        drawPath(path, defaultColor(), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
    }
}

// ─── 4. Zigzag ───────────────────────────────────────────────────────────────

class ZigzagWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Zigzag"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, h, amp) = waveBase(refreshState)
        val baseline = h - amp
        val segW = 40.dp.toPx()
        val animOffset = (animState[AnimSlot.PHASE_1000] / (2 * Math.PI.toFloat())) * segW * 2
        clipRect {
            val path = Path().apply {
                var x = -(animOffset % (segW * 2)); var goUp = true
                moveTo(x, h); lineTo(x, baseline)
                while (x <= w + segW) {
                    x += segW; lineTo(x, if (goUp) baseline - amp else baseline + amp); goUp = !goUp
                }
                lineTo(x, h); close()
            }
            drawPath(path, defaultColor().copy(alpha = 0.8f))
        }
    }
}

// ─── 5. Ripple rings ─────────────────────────────────────────────────────────

class RippleWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Ripple"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val maxR = (size.width / 2).coerceAtMost(size.height / 2) * progress
        val t = animState[AnimSlot.PHASE_1000] / (2 * Math.PI.toFloat())
        val center = Offset(size.width / 2, size.height / 2)
        repeat(5) { i ->
            val frac = ((t + i.toFloat() / 5) % 1f)
            drawCircle(defaultColor().copy(alpha = (1f - frac) * 0.7f), frac * maxR, center, style = Stroke(2.dp.toPx()))
        }
    }
}

// ─── 6. Square Wave ──────────────────────────────────────────────────────────

class SquareWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Square"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, h, amp) = waveBase(refreshState)
        val baseline = h - amp; val segW = 30.dp.toPx()
        val offset = (animState[AnimSlot.PHASE_1000] / (2 * Math.PI.toFloat())) * segW * 2
        clipRect {
            val path = Path().apply {
                var x = -(offset % (segW * 2)); var high = true
                moveTo(x, h); lineTo(x, baseline)
                while (x <= w + segW) {
                    val nx = x + segW; val y = if (high) baseline - amp else baseline + amp
                    lineTo(x, y); lineTo(nx, y); x = nx; high = !high
                }
                lineTo(x, h); close()
            }
            drawPath(path, defaultColor().copy(alpha = 0.8f))
        }
    }
}

// ─── 7. Bouncing dots ────────────────────────────────────────────────────────

class BouncingDotsWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Dots"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, h, amp) = waveBase(refreshState)
        val dotR = 6.dp.toPx() * refreshState.progress.coerceAtMost(1f)
        val phase = animState[AnimSlot.PHASE_1000].toDouble()
        repeat(12) { i ->
            val x = w * i.toFloat() / 11
            val y = h / 2 + sin(x / w * 2 * Math.PI + phase) * amp
            drawCircle(defaultColor().copy(alpha = 0.8f), dotR, Offset(x, y.toFloat()))
        }
    }
}

// ─── 8. Layered waves ────────────────────────────────────────────────────────

class LayeredWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Layered"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000, AnimSlot.PHASE_1400, AnimSlot.PHASE_1800)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, h, amp) = waveBase(refreshState)
        val bl = h - amp
        clipRect {
            listOf(
                Triple(animState[AnimSlot.PHASE_1000].toDouble(), 0.4f, bl + 4.dp.toPx()),
                Triple(animState[AnimSlot.PHASE_1400].toDouble(), 0.55f, bl),
                Triple(animState[AnimSlot.PHASE_1800].toDouble(), 0.7f, bl - 4.dp.toPx()),
            ).forEach { (phase, alpha, baseline) ->
                drawPath(sineFilledPath(baseline, h, w, amp, phase), defaultColor().copy(alpha = alpha))
            }
        }
    }
}

// ─── 9. Cosine ───────────────────────────────────────────────────────────────

class CosineWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Cosine"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, h, amp) = waveBase(refreshState)
        val baseline = h - amp; val phase = animState[AnimSlot.PHASE_1000].toDouble()
        clipRect {
            val path = Path().apply {
                moveTo(0f, h); lineTo(0f, baseline)
                var x = 0f
                while (x <= w) { lineTo(x, (baseline + cos(x / w * 2 * Math.PI + phase) * amp).toFloat()); x += 8f }
                lineTo(w, h); close()
            }
            drawPath(path, defaultColor().copy(alpha = 0.85f))
        }
    }
}

// ─── 10. Reverse (from top) ──────────────────────────────────────────────────

class ReverseWaveStyle : RefreshIndicatorStyle {
    override val key = "Wave.Reverse"
    override val requiredAnims = setOf(AnimSlot.PHASE_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val (w, _, amp) = waveBase(refreshState)
        val phase = animState[AnimSlot.PHASE_1000].toDouble()
        clipRect {
            val path = Path().apply {
                moveTo(0f, 0f); lineTo(0f, amp)
                var x = 0f
                while (x <= w) { lineTo(x, (amp + sin(x / w * 2 * Math.PI + phase) * amp).toFloat()); x += 8f }
                lineTo(w, 0f); close()
            }
            drawPath(path, defaultColor().copy(alpha = 0.85f))
        }
    }
}

// ─── Internal helper ─────────────────────────────────────────────────────────

/**
 * Placeholder — in real usage inject Color via constructor so styles remain
 * theme-agnostic without needing Compose context inside DrawScope.
 *
 * Pattern: pass `color: Color` into each class constructor (see README).
 */
private fun DrawScope.defaultColor() = androidx.compose.ui.graphics.Color(0xFF6650A4)