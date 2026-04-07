package com.ibrahim.composescrollrefresh.styles

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ibrahim.composescrollrefresh.RefreshScrollState

@Composable
fun SpringRefreshIndicator(
    state: RefreshScrollState,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary
) {
    val density = LocalDensity.current
    val height = with(density) { state.pullDistance.toDp() }
    
    // Scale effect based on progress, but with a "spring" feel when refreshing
    val scale by animateFloatAsState(
        targetValue = if (state.isRefreshing) 1.2f else state.progress.coerceAtMost(1f),
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val center = Offset(size.width / 2, size.height / 2)
            val baseRadius = 15.dp.toPx()
            val currentRadius = baseRadius * scale

            // Draw a "springy" circle
            drawCircle(
                color = color,
                radius = currentRadius,
                center = center
            )
            
            // Draw an outer ring
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = currentRadius + 10.dp.toPx() * (1f - scale.coerceIn(0f, 1f)),
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
        }
    }
}
