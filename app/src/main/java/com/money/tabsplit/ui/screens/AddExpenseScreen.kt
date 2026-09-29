package com.money.tabsplit.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.money.tabsplit.domain.model.Expense
import com.money.tabsplit.domain.model.ExpenseSplit
import com.money.tabsplit.domain.model.Person
import com.money.tabsplit.domain.model.SplitMode
import com.money.tabsplit.ui.components.Avatar
import com.money.tabsplit.ui.components.GradientPrimaryButton
import com.money.tabsplit.ui.components.NumericKeypad
import com.money.tabsplit.ui.theme.PrimaryGradientEnd
import com.money.tabsplit.ui.theme.PrimaryGradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    groupMembers: List<Person>,
    currencySymbol: String,
    existingExpense: Pair<Expense, List<ExpenseSplit>>? = null,
    onSave: (title: String, amount: Long, category: String, payerId: String, mode: SplitMode, participants: List<String>, inputs: Map<String, Double>) -> Unit,
    onClose: () -> Unit
) {
    var amountString by remember { mutableStateOf("0") }
    var title by remember { mutableStateOf(existingExpense?.first?.title ?: "") }
    var selectedCategory by remember { mutableStateOf(existingExpense?.first?.category ?: "General") }
    var selectedPayerId by remember { mutableStateOf(existingExpense?.first?.payerId ?: groupMembers.firstOrNull()?.id ?: "") }
    var splitMode by remember { mutableStateOf(SplitMode.EQUAL) }
    
    val participantIds = remember { mutableStateOf(groupMembers.map { it.id }.toSet()) }
    val splitInputs = remember { mutableStateMapOf<String, String>() }

    val amountLong = (amountString.toDoubleOrNull() ?: 0.0) * 100
    val amountCents = amountLong.toLong()

    val categories = listOf(
        "General" to Icons.Default.Category,
        "Food" to Icons.Default.Restaurant,
        "Transport" to Icons.Default.DirectionsCar,
        "Home" to Icons.Default.Home,
        "Fun" to Icons.Default.SportsEsports,
        "Shopping" to Icons.Default.ShoppingBag
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (existingExpense == null) "Add Expense" else "Edit Expense",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        bottomBar = {
            Box(modifier = Modifier.padding(20.dp)) {
                GradientPrimaryButton(
                    text = "Save Expense",
                    onClick = {
                        val inputsMap = splitInputs.mapValues { it.value.toDoubleOrNull() ?: 0.0 }
                        onSave(title, amountCents, selectedCategory, selectedPayerId, splitMode, participantIds.value.toList(), inputsMap)
                    },
                    enabled = title.isNotBlank() && amountCents > 0 && selectedPayerId.isNotEmpty() && participantIds.value.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$currencySymbol$amountString",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            NumericKeypad(
                onDigit = { digit ->
                    if (amountString == "0") amountString = digit.toString()
                    else if (amountString.substringAfter(".", "").length < 2) amountString += digit
                },
                onDecimal = {
                    if (!amountString.contains(".")) amountString += "."
                },
                onBackspace = {
                    if (amountString.length > 1) amountString = amountString.dropLast(1)
                    else amountString = "0"
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What was this for?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { (name, icon) ->
                        FilterChip(
                            selected = selectedCategory == name,
                            onClick = { selectedCategory = name },
                            label = { Text(name) },
                            leadingIcon = { Icon(icon, contentDescription = name) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Paid by", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groupMembers) { member ->
                        FilterChip(
                            selected = selectedPayerId == member.id,
                            onClick = { selectedPayerId = member.id },
                            label = { Text(member.name) },
                            leadingIcon = { Avatar(initials = member.name, size = 24.dp) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    SplitMode.values().forEach { mode ->
                        val isSelected = splitMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) Brush.horizontalGradient(listOf(PrimaryGradientStart, PrimaryGradientEnd))
                                    else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                                )
                                .clickable { splitMode = mode }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                groupMembers.forEach { member ->
                    val isIncluded = participantIds.value.contains(member.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Avatar(initials = member.name, size = 40.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(member.name, modifier = Modifier.weight(1f))
                        
                        if (isIncluded) {
                            when (splitMode) {
                                SplitMode.EQUAL -> {
                                    val partAmount = amountCents / participantIds.value.size.coerceAtLeast(1)
                                    Text(
                                        text = formatMoney(partAmount, currencySymbol),
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                }
                                SplitMode.EXACT -> {
                                    OutlinedTextField(
                                        value = splitInputs[member.id] ?: "",
                                        onValueChange = { splitInputs[member.id] = it },
                                        modifier = Modifier.width(100.dp).padding(end = 16.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                }
                                SplitMode.PERCENT -> {
                                    OutlinedTextField(
                                        value = splitInputs[member.id] ?: "",
                                        onValueChange = { splitInputs[member.id] = it },
                                        modifier = Modifier.width(80.dp).padding(end = 16.dp),
                                        trailingIcon = { Text("%") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                }
                                SplitMode.SHARES -> {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                                        val shares = (splitInputs[member.id]?.toIntOrNull() ?: 1)
                                        Box(modifier = Modifier.clickable { splitInputs[member.id] = (shares - 1).coerceAtLeast(1).toString() }.padding(8.dp)) { Text("-") }
                                        Text(shares.toString())
                                        Box(modifier = Modifier.clickable { splitInputs[member.id] = (shares + 1).toString() }.padding(8.dp)) { Text("+") }
                                    }
                                }
                            }
                        }
                        
                        Switch(
                            checked = isIncluded,
                            onCheckedChange = {
                                val newSet = participantIds.value.toMutableSet()
                                if (it) newSet.add(member.id) else newSet.remove(member.id)
                                participantIds.value = newSet
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
