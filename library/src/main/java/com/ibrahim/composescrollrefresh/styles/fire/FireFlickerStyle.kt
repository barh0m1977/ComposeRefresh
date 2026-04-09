package com.ibrahim.composescrollrefresh.styles.fire

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.ibrahim.composescrollrefresh.RefreshScrollState
import com.ibrahim.composescrollrefresh.core.AnimSlot
import com.ibrahim.composescrollrefresh.core.IndicatorAnimState
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import com.ibrahim.composescrollrefresh.core.get
import kotlin.math.sin

class FireFlickerStyle(
    private val fireColor: Color = Color(0xFFFF6B35),
    private val glowColor: Color = Color(0xFFFFD700),
) : RefreshIndicatorStyle {

    override val key = "Fire"

    // Declare only what we actually use — AnimProvider creates nothing else
    override val requiredAnims = setOf(AnimSlot.PHASE_1000, AnimSlot.PHASE_1400)

    override fun DrawScope.draw(
        refreshState: RefreshScrollState,
        animState: IndicatorAnimState,
    ) {
        val progress = refreshState.progress.coerceAtMost(1f)
        val w = size.width
        val h = size.height
        val flameH = h * 0.8f * progress
        val baseY = h

        // Draw 3 flame tongues at different widths / phases
        listOf(
            Triple(w / 2f,        flameH,        animState[AnimSlot.PHASE_1000]),
            Triple(w / 2f - 20.dp.toPx(), flameH * 0.75f, animState[AnimSlot.PHASE_1400]),
            Triple(w / 2f + 20.dp.toPx(), flameH * 0.65f, animState[AnimSlot.PHASE_1000] + 1f),
        ).forEachIndexed { i, (cx, fH, phase) ->
            val wobble = sin(phase.toDouble()).toFloat() * 8.dp.toPx()
            val path = Path().apply {
                moveTo(cx - 12.dp.toPx(), baseY)
                cubicTo(
                    cx - 16.dp.toPx(), baseY - fH * 0.3f,
                    cx + wobble - 8.dp.toPx(), baseY - fH * 0.6f,
                    cx + wobble, baseY - fH,
                )
                cubicTo(
                    cx + wobble + 8.dp.toPx(), baseY - fH * 0.6f,
                    cx + 16.dp.toPx(), baseY - fH * 0.3f,
                    cx + 12.dp.toPx(), baseY,
                )
                close()
            }
            val color = if (i == 0) fireColor else glowColor
            drawPath(path, color.copy(alpha = (0.9f - i * 0.2f) * progress))
        }

        // Glowing base
        drawOval(
            color = glowColor.copy(alpha = 0.3f * progress),
            topLeft = Offset(w / 2 - 20.dp.toPx(), baseY - 4.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(40.dp.toPx(), 8.dp.toPx()),
        )
    }
}