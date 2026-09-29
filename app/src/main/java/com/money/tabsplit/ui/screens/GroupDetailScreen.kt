package com.money.tabsplit.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.money.tabsplit.domain.model.Expense
import com.money.tabsplit.domain.model.ExpenseSplit
import com.money.tabsplit.domain.model.Person
import com.money.tabsplit.domain.model.PersonBalance
import com.money.tabsplit.domain.model.Settlement
import com.money.tabsplit.ui.components.Avatar
import com.money.tabsplit.ui.components.AvatarStack
import com.money.tabsplit.ui.components.BalanceBars
import com.money.tabsplit.ui.components.ConfettiAnimation
import com.money.tabsplit.ui.components.GradientPrimaryButton
import com.money.tabsplit.ui.components.SettleUpCard
import com.money.tabsplit.ui.theme.AccentCoral
import com.money.tabsplit.ui.theme.AccentMint
import com.money.tabsplit.ui.theme.PrimaryGradientEnd
import com.money.tabsplit.ui.theme.PrimaryGradientStart
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupName: String,
    currencySymbol: String,
    members: List<Person>,
    expenses: List<Pair<Expense, List<ExpenseSplit>>>,
    balances: List<PersonBalance>,
    settlements: List<Settlement>,
    onBack: () -> Unit,
    onAddExpense: () -> Unit,
    onDeleteExpense: (String) -> Unit,
    onMarkSettled: (String, String, Long) -> Unit
) {
    val tabs = listOf("Expenses", "Balances", "Settle up")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (pagerState.currentPage == 0) {
                ExtendedFloatingActionButton(
                    onClick = onAddExpense,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(listOf(PrimaryGradientStart, PrimaryGradientEnd)),
                            RoundedCornerShape(50)
                        )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Expense")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Expense")
                }
            }
        },
        topBar = {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.padding(end = 8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = groupName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    AvatarStack(
                        initialsList = members.map { it.name.take(2).uppercase() },
                        borderColor = MaterialTheme.colorScheme.background
                    )
                }
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> ExpensesTab(expenses, members, currencySymbol, snackbarHostState, onDeleteExpense, onAddExpense)
                1 -> BalancesTab(balances, members, currencySymbol)
                2 -> SettleUpTab(settlements, expenses.isNotEmpty(), members, currencySymbol, onMarkSettled)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesTab(
    expenses: List<Pair<Expense, List<ExpenseSplit>>>,
    members: List<Person>,
    currencySymbol: String,
    snackbarHostState: SnackbarHostState,
    onDeleteExpense: (String) -> Unit,
    onAddExpense: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    if (expenses.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No expenses yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            GradientPrimaryButton(text = "Add your first expense", onClick = onAddExpense, modifier = Modifier.fillMaxWidth(0.8f))
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)) {
            items(expenses, key = { it.first.id }) { (expense, _) ->
                val payerName = members.find { it.id == expense.payerId }?.name ?: "Someone"
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            onDeleteExpense(expense.id)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Expense deleted", actionLabel = "Undo")
                            }
                            true
                        } else false
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AccentCoral),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.padding(end = 24.dp))
                        }
                    },
                    content = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconTint = when (expense.category) {
                                "Food" -> AccentCoral
                                "Transport" -> AccentMint
                                "Home" -> PrimaryGradientStart
                                else -> MaterialTheme.colorScheme.primary
                            }
                            val iconImage = when (expense.category) {
                                "Food" -> Icons.Default.Restaurant
                                "Transport" -> Icons.Default.Flight
                                "Home" -> Icons.Default.Home
                                else -> Icons.Default.Receipt
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(iconTint.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(iconImage, contentDescription = expense.category, tint = iconTint)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(expense.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("paid by $payerName", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = formatMoney(expense.amount, currencySymbol),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BalancesTab(balances: List<PersonBalance>, members: List<Person>, currencySymbol: String) {
    val maxBalance = balances.maxOfOrNull { kotlin.math.abs(it.netBalance) }?.toFloat() ?: 1f

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)) {
        items(balances) { balance ->
            val person = members.find { it.id == balance.personId } ?: return@items
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(initials = person.name, size = 40.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(person.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    val amountColor = when {
                        balance.netBalance > 0 -> AccentMint
                        balance.netBalance < 0 -> AccentCoral
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    val amountText = when {
                        balance.netBalance > 0 -> "↑ " + formatMoney(balance.netBalance, currencySymbol)
                        balance.netBalance < 0 -> "↓ " + formatMoney(kotlin.math.abs(balance.netBalance), currencySymbol)
                        else -> formatMoney(0, currencySymbol)
                    }
                    Text(amountText, color = amountColor, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                BalanceBars(
                    owedToYou = if (balance.netBalance > 0) balance.netBalance.toFloat() else 0f,
                    youOwe = if (balance.netBalance < 0) kotlin.math.abs(balance.netBalance).toFloat() else 0f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SettleUpTab(
    settlements: List<Settlement>,
    hasExpenses: Boolean,
    members: List<Person>,
    currencySymbol: String,
    onMarkSettled: (String, String, Long) -> Unit
) {
    if (settlements.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)) {
            items(settlements) { settlement ->
                val fromName = members.find { it.id == settlement.fromPersonId }?.name ?: "User"
                val toName = members.find { it.id == settlement.toPersonId }?.name ?: "User"
                SettleUpCard(
                    user1Initials = fromName.take(2).uppercase(),
                    user2Initials = toName.take(2).uppercase(),
                    amount = formatMoney(settlement.amount, currencySymbol),
                    onSettle = { onMarkSettled(settlement.fromPersonId, settlement.toPersonId, settlement.amount) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    } else if (hasExpenses) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ConfettiAnimation(modifier = Modifier.fillMaxSize())
            Text(
                "All settled up! 🎉",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Add expenses to see settlements",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
