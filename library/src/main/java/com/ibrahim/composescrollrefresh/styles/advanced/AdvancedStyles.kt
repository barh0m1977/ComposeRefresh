package com.ibrahim.composescrollrefresh.styles.advanced

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
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

// ─── Shared helpers ──────────────────────────────────────────────────────────

private fun DrawScope.color() = androidx.compose.ui.graphics.Color(0xFF6650A4)

private fun DrawScope.arcTopLeft(cx: Float, cy: Float, r: Float) = Offset(cx - r, cy - r)
private fun arcSize(r: Float) = Size(r * 2, r * 2)

private fun RefreshScrollState.sweepAngle() = if (isRefreshing) 270f else 360f * progress.coerceIn(0f, 1f)
private fun RefreshScrollState.startAngle(rotation: Float) = if (isRefreshing) rotation else -90f

// ─── 1. Classic ──────────────────────────────────────────────────────────────

class ClassicAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Classic"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx(); val sw = 3.dp.toPx()
        drawArc(color(), refreshState.startAngle(animState[AnimSlot.ROTATION_1000]), refreshState.sweepAngle(), false, arcTopLeft(cx, cy, r), arcSize(r), style = Stroke(sw, cap = StrokeCap.Round))
        drawCircle(color().copy(alpha = 0.1f), r, Offset(cx, cy), style = Stroke(sw))
    }
}

// ─── 2. Dual arcs ────────────────────────────────────────────────────────────

class DualArcAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Dual"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000, AnimSlot.ROTATION_1400)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx(); val sw = 3.dp.toPx()
        drawArc(color(), refreshState.startAngle(animState[AnimSlot.ROTATION_1000]), refreshState.sweepAngle(), false, arcTopLeft(cx, cy, r), arcSize(r), style = Stroke(sw, cap = StrokeCap.Round))
        val r2 = r + 6.dp.toPx()
        drawArc(color().copy(alpha = 0.5f), refreshState.startAngle(animState[AnimSlot.ROTATION_1400]), refreshState.sweepAngle() * 0.7f, false, arcTopLeft(cx, cy, r2), arcSize(r2), style = Stroke(sw, cap = StrokeCap.Round))
    }
}

// ─── 3. Dots circle ──────────────────────────────────────────────────────────

class DotsCircleAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Dots"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx()
        val progress = refreshState.progress.coerceIn(0f, 1f)
        val baseAngle = Math.toRadians(if (refreshState.isRefreshing) animState[AnimSlot.ROTATION_1000].toDouble() else -90.0)
        repeat(12) { i ->
            val angle = baseAngle + i.toDouble() / 12 * 2 * PI
            val alpha = if (refreshState.isRefreshing) (i / 12f) * progress else progress
            drawCircle(color().copy(alpha = alpha * 0.9f), 3.dp.toPx() * progress, Offset(cx + cos(angle).toFloat() * r, cy + sin(angle).toFloat() * r))
        }
    }
}

// ─── 4. Clock ────────────────────────────────────────────────────────────────

class ClockAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Clock"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx()
        val progress = refreshState.progress.coerceIn(0f, 1f)
        drawCircle(color().copy(alpha = 0.15f * progress), r, Offset(cx, cy), style = Stroke(1.5f.dp.toPx()))
        val handAngle = if (refreshState.isRefreshing) animState[AnimSlot.ROTATION_1000] else -90f + 360f * progress
        val rad = Math.toRadians(handAngle.toDouble()).toFloat()
        drawLine(color().copy(alpha = 0.8f * progress), Offset(cx, cy), Offset(cx + cos(rad) * r * 0.8f, cy + sin(rad) * r * 0.8f), 3.dp.toPx())
        drawCircle(color(), 3.dp.toPx() * progress, Offset(cx, cy))
    }
}

// ─── 5. Segments ─────────────────────────────────────────────────────────────

class SegmentsAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Segments"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx()
        val progress = refreshState.progress.coerceIn(0f, 1f)
        val rot = if (refreshState.isRefreshing) animState[AnimSlot.ROTATION_1000] else 0f
        repeat(12) { i ->
            val base = i * 30f + rot
            val alpha = if (refreshState.isRefreshing) (i / 12f) * progress else progress
            drawArc(color().copy(alpha = alpha), base, 24f, false, arcTopLeft(cx, cy, r), arcSize(r), style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
        }
    }
}

// ─── 6. Radar ────────────────────────────────────────────────────────────────

class RadarAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Radar"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx()
        val progress = refreshState.progress.coerceIn(0f, 1f)
        val sweep = if (refreshState.isRefreshing) animState[AnimSlot.ROTATION_1000] else -90f + 360f * progress
        drawCircle(color().copy(alpha = 0.1f * progress), r, Offset(cx, cy), style = Stroke(1.5f.dp.toPx()))
        drawArc(color().copy(alpha = 0.3f * progress), if (refreshState.isRefreshing) animState[AnimSlot.ROTATION_1000] else -90f, -90f, true, arcTopLeft(cx, cy, r), arcSize(r))
        val rad = Math.toRadians(sweep.toDouble()).toFloat()
        drawLine(color().copy(alpha = 0.85f * progress), Offset(cx, cy), Offset(cx + cos(rad) * r, cy + sin(rad) * r), 3.dp.toPx())
    }
}

// ─── 7. Growing arc ──────────────────────────────────────────────────────────

class GrowingArcAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Growing"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000, AnimSlot.PULSE_1200)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx()
        val sweep = if (refreshState.isRefreshing) 60f + 240f * abs(sin(animState[AnimSlot.PULSE_1200] * PI.toFloat())) else refreshState.sweepAngle()
        drawArc(color(), refreshState.startAngle(animState[AnimSlot.ROTATION_1000]), sweep, false, arcTopLeft(cx, cy, r), arcSize(r), style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
    }
}

// ─── 8. Triple arc ───────────────────────────────────────────────────────────

class TripleArcAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Triple"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000, AnimSlot.ROTATION_1400, AnimSlot.ROTATION_700)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx(); val sw = 3.dp.toPx()
        listOf(
            Triple(r,              animState[AnimSlot.ROTATION_1000], color()),
            Triple(r + 7.dp.toPx(), animState[AnimSlot.ROTATION_1400], color().copy(alpha = 0.6f)),
            Triple(r + 14.dp.toPx(), animState[AnimSlot.ROTATION_700], color().copy(alpha = 0.35f)),
        ).forEach { (radius, rot, c) ->
            drawArc(c, refreshState.startAngle(rot), refreshState.sweepAngle(), false, arcTopLeft(cx, cy, radius), arcSize(radius), style = Stroke(sw, cap = StrokeCap.Round))
        }
    }
}

// ─── 9. Orbit ────────────────────────────────────────────────────────────────

class OrbitAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Orbit"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx()
        val progress = refreshState.progress.coerceIn(0f, 1f)
        drawCircle(color().copy(alpha = 0.15f * progress), r, Offset(cx, cy), style = Stroke(3.dp.toPx()))
        val angle = Math.toRadians(refreshState.startAngle(animState[AnimSlot.ROTATION_1000]).toDouble()).toFloat()
        drawCircle(color().copy(alpha = 0.9f * progress), 5.dp.toPx() * progress, Offset(cx + cos(angle) * r, cy + sin(angle) * r))
    }
}

// ─── 10. Material indeterminate ──────────────────────────────────────────────

class MaterialAdvancedStyle : RefreshIndicatorStyle {
    override val key = "Material"
    override val requiredAnims = setOf(AnimSlot.ROTATION_1000, AnimSlot.PULSE_1200)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val cx = size.width / 2; val cy = size.height / 2; val r = 15.dp.toPx(); val sw = 4.dp.toPx()
        val sweep = if (refreshState.isRefreshing) 30f + 220f * abs(sin(animState[AnimSlot.PULSE_1200] * PI.toFloat())) else refreshState.sweepAngle()
        val start = if (refreshState.isRefreshing) animState[AnimSlot.ROTATION_1000] * 2f else -90f
        drawArc(color().copy(alpha = 0.2f), 0f, 360f, false, arcTopLeft(cx, cy, r), arcSize(r), style = Stroke(sw, cap = StrokeCap.Round))
        drawArc(color(), start, sweep, false, arcTopLeft(cx, cy, r), arcSize(r), style = Stroke(sw + 1.dp.toPx(), cap = StrokeCap.Round))
    }
}