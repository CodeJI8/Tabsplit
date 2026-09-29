package com.money.tabsplit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.money.tabsplit.ui.theme.AccentCoral
import com.money.tabsplit.ui.theme.AccentMint

@Composable
fun BalanceBars(
    owedToYou: Float,
    youOwe: Float,
    modifier: Modifier = Modifier
) {
    // Both values should be positive
    val maxAmount = maxOf(owedToYou, youOwe, 1f) // avoid division by zero
    
    Canvas(modifier = modifier.fillMaxWidth().height(16.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2
        val barHeight = 8.dp.toPx()
        val cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)
        val centerPoint = canvasWidth / 2
        
        // Draw you owe (left side, coral)
        if (youOwe > 0) {
            val oweWidth = (youOwe / maxAmount) * (centerPoint - 16f) // 16f padding from center
            drawRoundRect(
                color = AccentCoral,
                topLeft = Offset(centerPoint - oweWidth, centerY - barHeight / 2),
                size = Size(oweWidth, barHeight),
                cornerRadius = cornerRadius
            )
        }
        
        // Draw owed to you (right side, mint)
        if (owedToYou > 0) {
            val getBackWidth = (owedToYou / maxAmount) * (centerPoint - 16f)
            drawRoundRect(
                color = AccentMint,
                topLeft = Offset(centerPoint, centerY - barHeight / 2),
                size = Size(getBackWidth, barHeight),
                cornerRadius = cornerRadius
            )
        }
        
        // Center tick
        drawRoundRect(
            color = Color.LightGray,
            topLeft = Offset(centerPoint - 1f, centerY - barHeight),
            size = Size(2f, barHeight * 2),
            cornerRadius = CornerRadius(1f, 1f)
        )
    }
}
