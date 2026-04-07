package com.ibrahim.composescrollrefresh.styles

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ibrahim.composescrollrefresh.RefreshScrollState
import kotlin.math.sin

@Composable
fun WaveRefreshIndicator(
    state: RefreshScrollState,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val density = LocalDensity.current
    val height = with(density) { state.pullDistance.toDp() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val width = size.width
            val canvasHeight = size.height
            val waveHeight = 20.dp.toPx() * state.progress.coerceAtMost(1f)
            val baseLine = canvasHeight - waveHeight

            clipRect {
                val path = Path().apply {
                    moveTo(0f, canvasHeight)
                    lineTo(0f, baseLine)
                    
                    val step = 10f
                    for (x in 0..width.toInt() step step.toInt()) {
                        val relativeX = x / width
                        val y = baseLine + sin(relativeX * 2 * Math.PI + phase) * waveHeight
                        lineTo(x.toFloat(), y.toFloat())
                    }
                    
                    lineTo(width, canvasHeight)
                    close()
                }
                drawPath(path, color)
            }
        }
    }
}
