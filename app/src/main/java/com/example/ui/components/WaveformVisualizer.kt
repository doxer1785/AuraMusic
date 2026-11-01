package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(
    amplitudes: List<Float>,
    isPlaying: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseFactor by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseFactor"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val totalBars = amplitudes.ifEmpty { List(24) { 0.3f } }.size
        val availableWidth = size.width
        val barWidth = (availableWidth / totalBars) * 0.65f
        val spacing = (availableWidth / totalBars) * 0.35f
        val canvasHeight = size.height

        val gradientBrush = Brush.verticalGradient(
            colors = listOf(primaryColor, secondaryColor)
        )

        amplitudes.ifEmpty { List(24) { 0.3f } }.forEachIndexed { index, rawAmp ->
            val dynamicAmp = if (isPlaying) {
                (rawAmp * if (index % 2 == 0) pulseFactor else (2f - pulseFactor)).coerceIn(0.15f, 1.0f)
            } else {
                (rawAmp * 0.3f).coerceAtLeast(0.1f)
            }

            val barHeight = canvasHeight * dynamicAmp
            val x = index * (barWidth + spacing)
            val y = (canvasHeight - barHeight) / 2f

            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
