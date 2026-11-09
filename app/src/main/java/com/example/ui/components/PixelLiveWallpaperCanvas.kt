package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

data class TouchRipple(
    val center: Offset,
    val startTime: Long = System.currentTimeMillis()
)

/**
 * High-performance, interactive original Pixel Dynamic Live Wallpaper.
 * Rendered using Compose Canvas with hardware-accelerated animated gradient mesh
 * and subtle touch-activated energy ripples.
 */
@Composable
fun PixelLiveWallpaperCanvas(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF0C1014)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pixel_live_wallpaper")

    // Dynamic wave animation offsets
    val wave1Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val wave2Progress by infiniteTransition.animateFloat(
        initialValue = (Math.PI).toFloat(),
        targetValue = (3 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val touchRipples = remember { mutableStateListOf<TouchRipple>() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("pixel_live_wallpaper_canvas")
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    touchRipples.add(TouchRipple(offset))
                    if (touchRipples.size > 5) {
                        touchRipples.removeAt(0)
                    }
                }
            }
    ) {
        val width = size.width
        val height = size.height

        // Base Dark Cosmic Background
        drawRect(color = baseColor)

        // Wave 1: Flowing Pixel Aurora Cyan Mesh
        val c1X = width * (0.3f + 0.2f * sin(wave1Progress.toDouble()).toFloat())
        val c1Y = height * (0.25f + 0.15f * cos(wave1Progress.toDouble()).toFloat())
        val r1 = width * 0.75f * pulseScale

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF006689).copy(alpha = 0.45f),
                    Color(0xFF00354A).copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(c1X, c1Y),
                radius = r1
            ),
            radius = r1,
            center = Offset(c1X, c1Y)
        )

        // Wave 2: Ambient Pixel Deep Purple Orb
        val c2X = width * (0.75f - 0.25f * cos(wave2Progress.toDouble()).toFloat())
        val c2Y = height * (0.70f + 0.18f * sin(wave2Progress.toDouble()).toFloat())
        val r2 = width * 0.85f * (2f - pulseScale)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4A2869).copy(alpha = 0.38f),
                    Color(0xFF221133).copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = Offset(c2X, c2Y),
                radius = r2
            ),
            radius = r2,
            center = Offset(c2X, c2Y)
        )

        // Wave 3: Subdued Mint Aurora Glow at Bottom Edge
        val c3X = width * (0.5f + 0.3f * sin((wave1Progress * 0.7f).toDouble()).toFloat())
        val c3Y = height * 0.90f
        val r3 = width * 0.65f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1B5E35).copy(alpha = 0.30f),
                    Color.Transparent
                ),
                center = Offset(c3X, c3Y),
                radius = r3
            ),
            radius = r3,
            center = Offset(c3X, c3Y)
        )

        // Render Active Touch Ripples
        val currentTime = System.currentTimeMillis()
        val iterator = touchRipples.iterator()
        while (iterator.hasNext()) {
            val ripple = iterator.next()
            val age = currentTime - ripple.startTime
            if (age > 1200) {
                iterator.remove()
            } else {
                val progress = age / 1200f
                val radius = 40.dp.toPx() + progress * 160.dp.toPx()
                val alpha = (1f - progress).coerceIn(0f, 1f) * 0.35f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF70D2FF).copy(alpha = alpha),
                            Color.Transparent
                        ),
                        center = ripple.center,
                        radius = radius
                    ),
                    radius = radius,
                    center = ripple.center
                )
            }
        }
    }
}
