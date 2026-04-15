package com.ibrahim.composescrollrefresh.styles.spring

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
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



private fun pseudoScale(state: RefreshScrollState) =
    if (state.isRefreshing) 1.2f else state.progress.coerceAtMost(1f)

private fun DrawScope.color() = androidx.compose.ui.graphics.Color(0xFF7D5260)

// ─── 1. Classic springy circle ───────────────────────────────────────────────

class ClassicSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Classic"
    override val requiredAnims = emptySet<AnimSlot>()

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val scale = pseudoScale(refreshState)
        val center = Offset(size.width / 2, size.height / 2)
        val r = 15.dp.toPx() * scale
        drawCircle(color(), r, center)
        drawCircle(color().copy(alpha = 0.3f), r + 10.dp.toPx() * (1f - scale.coerceIn(0f, 1f)), center, style = Stroke(2.dp.toPx()))
    }
}

// ─── 2. Coil Spring ──────────────────────────────────────────────────────────

class CoilSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Coil"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f); if (progress < 0.05f) return
        val scale = pseudoScale(refreshState)
        val cx = size.width / 2; val cy = size.height / 2
        val coilH = size.height * 0.7f * progress
        val coilW = 20.dp.toPx() * scale
        val top = cy - coilH / 2
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val loops = 5; val steps = loops * 20
        val path = Path()
        for (i in 0..steps) {
            val frac = i.toFloat() / steps
            val x = cx + cos(frac * loops * 2 * PI.toFloat() + t * 2 * PI.toFloat()) * coilW
            val y = top + frac * coilH
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color().copy(alpha = 0.85f), style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
    }
}

// ─── 3. Bouncing ball ────────────────────────────────────────────────────────

class BouncingBallSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Ball"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val scale = pseudoScale(refreshState)
        val cx = size.width / 2; val cy = size.height / 2
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val bounce = abs(sin(t * PI.toFloat()))
        val r = 12.dp.toPx() * progress
        val squash = 1f - bounce * 0.3f * scale
        val ballY = cy - size.height * 0.3f * bounce * scale
        drawOval(color().copy(alpha = 0.85f * progress), Offset(cx - r, ballY - r * squash), Size(r * 2, r * 2 * squash))
        val shadowR = r * (1f - bounce * 0.5f) * 0.6f
        drawOval(color().copy(alpha = 0.3f * progress), Offset(cx - shadowR, cy + r - 2.dp.toPx()), Size(shadowR * 2, 4.dp.toPx()))
    }
}

// ─── 4. Elastic bar ──────────────────────────────────────────────────────────

class ElasticBarSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Bar"
    override val requiredAnims = emptySet<AnimSlot>()

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val scale = pseudoScale(refreshState)
        val cx = size.width / 2; val cy = size.height / 2
        val barH = 8.dp.toPx() * progress
        val barW = (size.width * 0.6f * scale).coerceAtMost(size.width * 0.9f)
        val squishY = cy + 5.dp.toPx() * (scale - 1f) * 2
        drawRoundRect(color().copy(alpha = 0.8f * progress), Offset(cx - barW / 2, squishY - barH / 2), Size(barW, barH), CornerRadius(barH / 2))
    }
}

// ─── 5. Trampoline arc ───────────────────────────────────────────────────────

class TrampolineSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Trampoline"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val scale = pseudoScale(refreshState)
        val cx = size.width / 2; val cy = size.height / 2
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val deflect = 15.dp.toPx() * abs(sin(t * PI.toFloat())) * scale
        val sw = 3.dp.toPx()
        val arm = 40.dp.toPx()
        val path = Path().apply { moveTo(cx - arm, cy); quadraticBezierTo(cx, cy + deflect, cx + arm, cy) }
        drawPath(path, color().copy(alpha = 0.7f * progress), style = Stroke(sw * 1.5f, cap = StrokeCap.Round))
        drawLine(color().copy(alpha = 0.5f * progress), Offset(cx - arm, cy), Offset(cx - arm, cy + 12.dp.toPx()), sw)
        drawLine(color().copy(alpha = 0.5f * progress), Offset(cx + arm, cy), Offset(cx + arm, cy + 12.dp.toPx()), sw)
    }
}

// ─── 6. Star burst ───────────────────────────────────────────────────────────

class StarBurstSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Star"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val scale = pseudoScale(refreshState)
        val cx = size.width / 2; val cy = size.height / 2
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val arms = 6; val innerR = 6.dp.toPx() * progress
        val outerR = (6.dp.toPx() + 14.dp.toPx() * abs(sin(t * 2 * PI.toFloat()))) * scale * progress
        val path = Path()
        for (i in 0 until arms * 2) {
            val angle = i.toFloat() / (arms * 2) * 2 * PI.toFloat() - PI.toFloat() / 2
            val r = if (i % 2 == 0) outerR else innerR
            val x = cx + cos(angle) * r; val y = cy + sin(angle) * r
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close(); drawPath(path, color().copy(alpha = 0.8f * progress))
    }
}

// ─── 7. Concentric rings ─────────────────────────────────────────────────────

class ConcentricSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Rings"
    override val requiredAnims = emptySet<AnimSlot>()

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val scale = pseudoScale(refreshState)
        val center = Offset(size.width / 2, size.height / 2)
        repeat(4) { i ->
            val frac = (i + 1).toFloat() / 4
            drawCircle(color().copy(alpha = (1f - frac * 0.5f) * progress), 8.dp.toPx() * frac * scale * progress, center, style = Stroke(3.dp.toPx()))
        }
    }
}

// ─── 8. Jelly ────────────────────────────────────────────────────────────────

class JellySpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Jelly"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val scale = pseudoScale(refreshState)
        val cx = size.width / 2; val cy = size.height / 2
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val sx = 1f + 0.3f * abs(sin(t * 2 * PI.toFloat())) * scale
        val sy = 1f / sx
        val r = 14.dp.toPx() * progress
        drawOval(color().copy(alpha = 0.8f * progress), Offset(cx - r * sx, cy - r * sy), Size(r * 2 * sx, r * 2 * sy))
    }
}

// ─── 9. Pendulum ─────────────────────────────────────────────────────────────

class PendulumSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Pendulum"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val scale = pseudoScale(refreshState)
        val cx = size.width / 2; val cy = size.height / 2
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val armLen = 30.dp.toPx() * progress
        val pivotY = cy - armLen * 0.5f
        val rad = (sin(t * 2 * PI.toFloat()) * 40f * scale * progress).let { Math.toRadians(it.toDouble()).toFloat() }
        val bobX = cx + sin(rad) * armLen; val bobY = pivotY + cos(rad) * armLen
        drawLine(color().copy(alpha = 0.5f * progress), Offset(cx, pivotY), Offset(bobX, bobY), 3.dp.toPx())
        drawCircle(color().copy(alpha = 0.85f * progress), 7.dp.toPx() * progress, Offset(bobX, bobY))
    }
}

// ─── 10. Rubber band ─────────────────────────────────────────────────────────

class RubberBandSpringStyle : RefreshIndicatorStyle {
    override val key = "Spring.Rubber"
    override val requiredAnims = setOf(AnimSlot.FLOAT_2000)

    override fun DrawScope.draw(refreshState: RefreshScrollState, animState: IndicatorAnimState) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val scale = pseudoScale(refreshState)
        val cx = size.width / 2; val cy = size.height / 2
        val t = animState[AnimSlot.FLOAT_2000] / 100f
        val snap = abs(sin(t * PI.toFloat()))
        val pullY = cy + 20.dp.toPx() * snap * scale
        val arm = 40.dp.toPx()
        val sw = (3.dp.toPx() + 2.dp.toPx() * snap)
        val path = Path().apply { moveTo(cx - arm, cy); quadraticBezierTo(cx, pullY, cx + arm, cy) }
        drawPath(path, color().copy(alpha = 0.75f * progress), style = Stroke(sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color().copy(alpha = 0.6f * progress), 4.dp.toPx() * progress, Offset(cx - arm, cy))
        drawCircle(color().copy(alpha = 0.6f * progress), 4.dp.toPx() * progress, Offset(cx + arm, cy))
    }
}