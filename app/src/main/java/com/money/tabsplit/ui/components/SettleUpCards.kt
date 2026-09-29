package com.money.tabsplit.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.money.tabsplit.ui.theme.AccentMint
import com.money.tabsplit.ui.theme.Shapes
import kotlin.math.roundToInt

@Composable
fun SettleUpCard(
    user1Initials: String,
    user2Initials: String,
    amount: String,
    onSettle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = Shapes.medium, // 24dp radius
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Avatar(initials = user1Initials, size = 56.dp)
                
                Spacer(modifier = Modifier.width(16.dp))
                
                AnimatedArrow()
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Avatar(initials = user2Initials, size = 56.dp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = amount,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SwipeToConfirmButton(onConfirm = onSettle)
        }
    }
}

@Composable
fun AnimatedArrow() {
    val infiniteTransition = rememberInfiniteTransition(label = "arrow")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "arrowOffset"
    )
    
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Pays",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .size(32.dp)
            .offset(x = offsetX.dp)
    )
}

@Composable
fun SwipeToConfirmButton(
    onConfirm: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var width by remember { mutableFloatStateOf(0f) }
    var isConfirmed by remember { mutableStateOf(false) }
    val thumbSize = 48.dp
    val thumbSizePx = with(LocalDensity.current) { thumbSize.toPx() }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(Shapes.small) // fully rounded
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onSizeChanged { width = it.width.toFloat() },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = if (isConfirmed) "Settled!" else "Swipe to settle",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Center)
        )
        
        val dragModifier = if (isConfirmed) Modifier else Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (offsetX > width - thumbSizePx * 1.5f) {
                        offsetX = width - thumbSizePx - 8f // 8f is padding
                        isConfirmed = true
                        onConfirm()
                    } else {
                        offsetX = 0f
                    }
                }
            ) { change, dragAmount ->
                change.consume()
                offsetX = (offsetX + dragAmount).coerceIn(0f, width - thumbSizePx - 8f)
            }
        }
        
        Surface(
            modifier = dragModifier
                .padding(4.dp)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .size(thumbSize),
            shape = CircleShape,
            color = if (isConfirmed) AccentMint else MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isConfirmed) Icons.Filled.Check else Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Swipe",
                    tint = Color.White
                )
            }
        }
    }
}
