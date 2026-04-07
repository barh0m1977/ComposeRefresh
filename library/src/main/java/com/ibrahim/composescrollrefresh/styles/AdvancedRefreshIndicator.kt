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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ibrahim.composescrollrefresh.RefreshScrollState

@Composable
fun AdvancedRefreshIndicator(
    state: RefreshScrollState,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val density = LocalDensity.current
    val height = with(density) { state.pullDistance.toDp() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "advanced")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val center = Offset(size.width / 2, size.height / 2)
            val arcSize = 30.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            
            val sweepAngle = if (state.isRefreshing) {
                270f
            } else {
                360f * state.progress.coerceIn(0f, 1f)
            }
            
            val startAngle = if (state.isRefreshing) {
                rotation
            } else {
                -90f
            }

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - arcSize / 2, center.y - arcSize / 2),
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Draw a subtle background circle
            drawCircle(
                color = color.copy(alpha = 0.1f),
                radius = arcSize / 2,
                center = center,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
