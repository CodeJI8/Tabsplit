package com.money.tabsplit.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.money.tabsplit.TabsplitApplication
import com.money.tabsplit.ui.screens.AddExpenseScreen
import com.money.tabsplit.ui.screens.GroupDetailScreen
import com.money.tabsplit.ui.screens.HomeScreen
import com.money.tabsplit.ui.screens.NewGroupSheet
import com.money.tabsplit.ui.screens.SettingsScreen

@Composable
fun TabsplitNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as TabsplitApplication
    
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(app, app.repository)
    )
    
    val themeMode by viewModel.themeMode.collectAsState()
    val defaultCurrency by viewModel.defaultCurrency.collectAsState()
    val totalOwedToYou by viewModel.totalOwedToYou.collectAsState()
    val totalYouOwe by viewModel.totalYouOwe.collectAsState()
    val groupsWithMembers by viewModel.groupsWithMembers.collectAsState()

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("tabsplit_prefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("demo_seeded", false)) {
            viewModel.seedDemoData()
            prefs.edit().putBoolean("demo_seeded", true).apply()
        }
    }
    
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        // Handle export
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        // Handle import
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                var showNewGroupSheet by remember { mutableStateOf(false) }

                HomeScreen(
                    totalOwedToYou = totalOwedToYou,
                    totalYouOwe = totalYouOwe,
                    groups = groupsWithMembers,
                    currencySymbol = defaultCurrency,
                    onGroupClick = { groupId ->
                        viewModel.selectGroup(groupId)
                        navController.navigate("group/$groupId")
                    },
                    onNewGroupClick = { showNewGroupSheet = true },
                    onSettingsClick = { navController.navigate("settings") }
                )

                if (showNewGroupSheet) {
                    NewGroupSheet(
                        onDismiss = { showNewGroupSheet = false },
                        onCreate = { name, currency, icon, memberNames ->
                            viewModel.createGroup(name, currency, icon, "blue", memberNames)
                            showNewGroupSheet = false
                        }
                    )
                }
            }
            composable("group/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")
                if (groupId != null) {
                    LaunchedEffect(groupId) {
                        viewModel.selectGroup(groupId)
                    }
                }
                val selectedGroup by viewModel.selectedGroup.collectAsState()
                val groupMembers by viewModel.groupMembers.collectAsState()
                val groupExpenses by viewModel.groupExpenses.collectAsState()
                val groupExpenseSplits by viewModel.groupExpenseSplits.collectAsState()
                val groupBalances by viewModel.groupBalances.collectAsState()
                val settlements by viewModel.settlements.collectAsState()

                val expensesWithSplits = groupExpenses.map { expense ->
                    expense to (groupExpenseSplits[expense.id] ?: emptyList())
                }

                GroupDetailScreen(
                    groupName = selectedGroup?.name ?: "",
                    currencySymbol = selectedGroup?.currencySymbol ?: defaultCurrency,
                    members = groupMembers,
                    expenses = expensesWithSplits,
                    balances = groupBalances,
                    settlements = settlements,
                    onBack = { navController.popBackStack() },
                    onAddExpense = { if (groupId != null) navController.navigate("add_expense/$groupId") },
                    onDeleteExpense = { expenseId -> viewModel.deleteExpense(expenseId) },
                    onMarkSettled = { fromId, toId, amount -> viewModel.markSettled(fromId, toId, amount) }
                )
            }
            composable("add_expense/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")
                if (groupId != null) {
                    LaunchedEffect(groupId) {
                        viewModel.selectGroup(groupId)
                    }
                }
                val groupMembers by viewModel.groupMembers.collectAsState()
                val selectedGroup by viewModel.selectedGroup.collectAsState()

                AddExpenseScreen(
                    groupMembers = groupMembers,
                    currencySymbol = selectedGroup?.currencySymbol ?: defaultCurrency,
                    existingExpense = null,
                    onSave = { title, amount, category, payerId, mode, participants, inputs ->
                        viewModel.addExpense(title, amount, category, payerId, mode, participants, inputs)
                        navController.popBackStack()
                    },
                    onClose = { navController.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    themeMode = themeMode,
                    onThemeChange = { viewModel.setThemeMode(it) },
                    currency = defaultCurrency,
                    onCurrencyChange = { viewModel.setDefaultCurrency(it) },
                    onExportBackup = { exportLauncher.launch("tabsplit_backup.json") },
                    onImportBackup = { importLauncher.launch(arrayOf("application/json")) },
                    onClearDemo = { viewModel.clearDemoData() },
                    onBack = { navController.popBackStack() },
                    onShareGroupSummary = null
                )
            }
        }
    }
}
