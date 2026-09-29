package com.money.tabsplit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.money.tabsplit.ui.theme.PrimaryGradientStart

@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    backgroundColor: Color = PrimaryGradientStart,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(2).uppercase(),
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun AvatarStack(
    initialsList: List<String>,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    overlap: Dp = 16.dp,
    borderColor: Color = Color.White // Will be overridden or set appropriately
) {
    Row(modifier = modifier) {
        initialsList.take(4).forEachIndexed { index, initials ->
            Avatar(
                initials = initials,
                size = size,
                modifier = Modifier
                    .offset(x = -(overlap * index))
                    .border(2.dp, borderColor, CircleShape)
            )
        }
        if (initialsList.size > 4) {
            Avatar(
                initials = "+${initialsList.size - 4}",
                size = size,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .offset(x = -(overlap * 4))
                    .border(2.dp, borderColor, CircleShape)
            )
        }
    }
}
