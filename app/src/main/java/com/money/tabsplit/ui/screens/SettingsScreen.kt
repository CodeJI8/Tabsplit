package com.money.tabsplit.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.money.tabsplit.ui.components.OutlineButton
import com.money.tabsplit.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: String,
    onThemeChange: (String) -> Unit,
    currency: String,
    onCurrencyChange: (String) -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onClearDemo: () -> Unit,
    onBack: () -> Unit,
    onShareGroupSummary: (() -> Unit)? = null
) {
    var showClearConfirm by remember { mutableStateOf(false) }
    
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear Demo Data") },
            text = { Text("Are you sure you want to clear demo data?") },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirm = false
                    onClearDemo()
                }) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    // Back icon can be added here
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Theme", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("light", "dark", "system").forEach { mode ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .selectable(
                                    selected = (themeMode == mode),
                                    onClick = { onThemeChange(mode) },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (themeMode == mode),
                                onClick = null // null recommended for accessibility with row
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = mode.replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }

            Card(
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Default Currency", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("$", "€", "£", "¥", "₹").forEach { c ->
                            FilterChip(
                                selected = (currency == c),
                                onClick = { onCurrencyChange(c) },
                                label = { Text(c) }
                            )
                        }
                    }
                }
            }
            
            Card(
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Backup & Restore", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlineButton("Export Backup", onClick = onExportBackup, modifier = Modifier.weight(1f))
                        OutlineButton("Import Backup", onClick = onImportBackup, modifier = Modifier.weight(1f))
                    }
                }
            }

            Card(
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Data", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showClearConfirm = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Clear Demo Data")
                    }
                    if (onShareGroupSummary != null) {
                        Button(onClick = onShareGroupSummary, modifier = Modifier.fillMaxWidth()) {
                            Text("Share Group Summary")
                        }
                    }
                }
            }

            Card(
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tabsplit", style = MaterialTheme.typography.titleMedium)
                    Text("Version 1.0", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text("No permissions required", modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}
