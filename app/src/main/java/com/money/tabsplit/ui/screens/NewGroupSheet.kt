package com.money.tabsplit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.money.tabsplit.ui.components.GradientPrimaryButton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewGroupSheet(
    onDismiss: () -> Unit,
    onCreate: (name: String, currency: String, icon: String, members: List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("home") }
    var selectedCurrency by remember { mutableStateOf("$") }
    var currentMemberName by remember { mutableStateOf("") }
    val members = remember { mutableStateListOf<String>() }

    val icons = listOf(
        "trip" to Icons.Default.Flight,
        "home" to Icons.Default.Home,
        "food" to Icons.Default.Restaurant,
        "party" to Icons.Default.Celebration,
        "work" to Icons.Default.Work
    )
    val currencies = listOf("$", "€", "£", "¥", "₹")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "New Group",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Icon", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(icons) { (name, icon) ->
                    FilterChip(
                        selected = selectedIcon == name,
                        onClick = { selectedIcon = name },
                        label = { Text(name.replaceFirstChar { it.uppercase() }) },
                        leadingIcon = { Icon(icon, contentDescription = name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Currency", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                currencies.forEach { curr ->
                    FilterChip(
                        selected = selectedCurrency == curr,
                        onClick = { selectedCurrency = curr },
                        label = { Text(curr) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Members", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = currentMemberName,
                onValueChange = { currentMemberName = it },
                label = { Text("Add member") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (currentMemberName.isNotBlank() && !members.contains(currentMemberName.trim())) {
                            members.add(currentMemberName.trim())
                            currentMemberName = ""
                        }
                    }
                )
            )
            
            if (members.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.forEach { member ->
                        InputChip(
                            selected = false,
                            onClick = { },
                            label = { Text(member) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier
                                        .size(InputChipDefaults.AvatarSize)
                                        .clickable { members.remove(member) }
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            GradientPrimaryButton(
                text = "Create Group",
                onClick = {
                    onCreate(groupName, selectedCurrency, selectedIcon, members.toList())
                },
                enabled = groupName.isNotBlank() && members.size >= 2,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
