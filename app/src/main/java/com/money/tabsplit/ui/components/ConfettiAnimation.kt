package com.money.tabsplit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.money.tabsplit.ui.theme.AccentCoral
import com.money.tabsplit.ui.theme.AccentMint
import com.money.tabsplit.ui.theme.PrimaryGradientEnd
import com.money.tabsplit.ui.theme.PrimaryGradientStart
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    var color: Color,
    var rotation: Float,
    var alpha: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiAnimation(modifier: Modifier = Modifier) {
    var particles by remember { mutableStateOf<List<Particle>>(emptyList()) }
    var isInitialized by remember { mutableStateOf(false) }
    
    LaunchedEffect(isInitialized) {
        if (!isInitialized) return@LaunchedEffect
        val startTime = System.nanoTime()
        while (true) {
            val frameTime = withFrameNanos { it }
            val timeMs = (frameTime - startTime) / 1_000_000
            
            if (timeMs > 3000) break
            
            particles = particles.map { p ->
                p.copy(
                    x = p.x + p.vx,
                    y = p.y + p.vy,
                    vy = p.vy + 0.5f,
                    rotation = p.rotation + p.vx * 2f,
                    alpha = (1f - (timeMs / 3000f)).coerceIn(0f, 1f)
                )
            }
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        if (!isInitialized) {
            val colors = listOf(AccentMint, AccentCoral, PrimaryGradientStart, PrimaryGradientEnd, Color.Yellow, Color.Cyan)
            particles = List(60) {
                Particle(
                    x = size.width / 2f,
                    y = size.height * 0.1f,
                    vx = (Random.nextFloat() - 0.5f) * 20f,
                    vy = (Random.nextFloat() - 0.5f) * 10f - 10f,
                    size = 15f + Random.nextFloat() * 20f,
                    color = colors.random(),
                    rotation = Random.nextFloat() * 360f,
                    alpha = 1f,
                    isCircle = Random.nextBoolean()
                )
            }
            isInitialized = true
        }
        
        particles.forEach { p ->
            if (p.alpha > 0) {
                rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = p.alpha),
                            radius = p.size / 2f,
                            center = Offset(p.x, p.y)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = p.alpha),
                            topLeft = Offset(p.x - p.size / 2f, p.y - p.size / 2f),
                            size = Size(p.size, p.size)
                        )
                    }
                }
            }
        }
    }
}
