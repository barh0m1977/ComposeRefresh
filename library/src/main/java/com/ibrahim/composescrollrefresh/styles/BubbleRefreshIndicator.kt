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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ibrahim.composescrollrefresh.RefreshScrollState
import kotlin.random.Random

@Composable
fun BubbleRefreshIndicator(
    state: RefreshScrollState,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary
) {
    val density = LocalDensity.current
    val height = with(density) { state.pullDistance.toDp() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "bubble")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "float"
    )

    val bubbles = remember {
        List(15) {
            BubbleData(
                xRel = Random.nextFloat(),
                yRel = Random.nextFloat(),
                sizeRel = Random.nextFloat() * 0.5f + 0.5f,
                speed = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val width = size.width
            val canvasHeight = size.height

            bubbles.forEach { bubble ->
                val x = bubble.xRel * width
                val baseSize = 10.dp.toPx() * bubble.sizeRel
                val currentSize = baseSize * state.progress.coerceAtMost(1f)
                
                // Floating effect while refreshing
                val yOffset = if (state.isRefreshing) {
                    ((floatOffset * bubble.speed) % canvasHeight)
                } else {
                    0f
                }
                
                val y = (bubble.yRel * canvasHeight - yOffset + canvasHeight) % canvasHeight

                drawCircle(
                    color = color.copy(alpha = 0.6f * state.progress.coerceAtMost(1f)),
                    radius = currentSize,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private data class BubbleData(
    val xRel: Float,
    val yRel: Float,
    val sizeRel: Float,
    val speed: Float
)
