package com.money.tabsplit.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.money.tabsplit.domain.model.Group
import com.money.tabsplit.domain.model.Person
import com.money.tabsplit.ui.components.GradientPrimaryButton
import com.money.tabsplit.ui.theme.AccentCoral
import com.money.tabsplit.ui.theme.AccentMint
import com.money.tabsplit.ui.theme.PrimaryGradientEnd
import com.money.tabsplit.ui.theme.PrimaryGradientStart
import java.util.Calendar

data class GroupWithMembers(val group: Group, val members: List<Person>, val balance: Long)

fun formatMoney(cents: Long, symbol: String): String {
    val amount = kotlin.math.abs(cents) / 100.0
    val formatted = "%.2f".format(amount)
    return when {
        cents > 0 -> "+$symbol$formatted"
        cents < 0 -> "-$symbol$formatted"
        else -> "$symbol$formatted"
    }
}

@Composable
fun HomeScreen(
    totalOwedToYou: Long,
    totalYouOwe: Long,
    groups: List<GroupWithMembers>,
    currencySymbol: String = "$",
    onGroupClick: (String) -> Unit,
    onNewGroupClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val netBalance = totalOwedToYou - totalYouOwe

    Scaffold(
        floatingActionButton = {
            if (groups.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onNewGroupClick,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(listOf(PrimaryGradientStart, PrimaryGradientEnd)),
                            RoundedCornerShape(50)
                        )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "New Group")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Group")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Custom Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val greeting = when (hour) {
                    in 0..11 -> "Good morning"
                    in 12..16 -> "Good afternoon"
                    else -> "Good evening"
                }
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(PrimaryGradientStart, PrimaryGradientEnd)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Net Balance",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    val animatedBalance by animateFloatAsState(
                        targetValue = (netBalance / 100.0).toFloat(),
                        animationSpec = tween(1000),
                        label = "balanceAnimation"
                    )
                    
                    val balanceString = if (animatedBalance >= 0) {
                        "$currencySymbol${"%.2f".format(animatedBalance)}"
                    } else {
                        "-$currencySymbol${"%.2f".format(kotlin.math.abs(animatedBalance))}"
                    }

                    Text(
                        text = balanceString,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "You're owed",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "+$currencySymbol${"%.2f".format(totalOwedToYou / 100.0)}",
                                color = AccentMint,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "You owe",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "-$currencySymbol${"%.2f".format(totalYouOwe / 100.0)}",
                                color = AccentCoral,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Your groups",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (groups.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawRoundRect(
                            color = PrimaryGradientStart,
                            topLeft = Offset(20f, 30f),
                            size = Size(200f, 140f),
                            cornerRadius = CornerRadius(16f, 16f),
                            style = Stroke(width = 8f)
                        )
                        drawLine(
                            color = PrimaryGradientStart,
                            start = Offset(20f, 70f),
                            end = Offset(220f, 70f),
                            strokeWidth = 8f
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "No groups yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Create a group to start splitting bills.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    GradientPrimaryButton(
                        text = "Create your first group",
                        onClick = onNewGroupClick,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(groups) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGroupClick(item.group.id) },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimaryGradientStart.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.group.name.take(1).uppercase(),
                                        color = PrimaryGradientStart,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.group.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${item.members.size} members",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (item.balance != 0L) {
                                    val isPositive = item.balance > 0
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isPositive) AccentMint.copy(alpha = 0.1f) else AccentCoral.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = formatMoney(item.balance, item.group.currencySymbol),
                                            color = if (isPositive) AccentMint else AccentCoral,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Settled up",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
