package com.money.tabsplit.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.money.tabsplit.domain.math.SettlementMinimizer
import com.money.tabsplit.domain.math.SplitMath
import com.money.tabsplit.domain.model.Expense
import com.money.tabsplit.domain.model.ExpenseSplit
import com.money.tabsplit.domain.model.Group
import com.money.tabsplit.domain.model.Person
import com.money.tabsplit.domain.model.PersonBalance
import com.money.tabsplit.domain.model.Settlement
import com.money.tabsplit.domain.model.SplitMode
import com.money.tabsplit.domain.repository.TabsplitRepository
import com.money.tabsplit.ui.screens.GroupWithMembers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    application: Application,
    private val repository: TabsplitRepository
) : AndroidViewModel(application) {

    private val prefs: SharedPreferences =
        application.getSharedPreferences("tabsplit_prefs", Context.MODE_PRIVATE)

    val themeMode = MutableStateFlow(prefs.getString("themeMode", "system") ?: "system")
    val defaultCurrency = MutableStateFlow(prefs.getString("defaultCurrency", "$") ?: "$")

    fun setThemeMode(mode: String) {
        prefs.edit().putString("themeMode", mode).apply()
        themeMode.value = mode
    }

    fun setDefaultCurrency(currency: String) {
        prefs.edit().putString("defaultCurrency", currency).apply()
        defaultCurrency.value = currency
    }

    val allGroups: StateFlow<List<Group>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedGroupId = MutableStateFlow<String?>(null)

    val selectedGroup: StateFlow<Group?> = selectedGroupId
        .flatMapLatest { id ->
            if (id != null) repository.getGroupById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val groupMembers: StateFlow<List<Person>> = selectedGroupId
        .flatMapLatest { id ->
            if (id != null) repository.getPersonsForGroup(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupExpenses: StateFlow<List<Expense>> = selectedGroupId
        .flatMapLatest { id ->
            if (id != null) repository.getExpensesForGroup(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupExpenseSplits: StateFlow<Map<String, List<ExpenseSplit>>> = groupExpenses
        .flatMapLatest { expenses ->
            if (expenses.isEmpty()) {
                flowOf(emptyMap())
            } else {
                val flows = expenses.map { expense ->
                    repository.getSplitsForExpense(expense.id).map { splits -> expense.id to splits }
                }
                combine(flows) { array ->
                    array.toMap()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val groupBalances: StateFlow<List<PersonBalance>> = combine(
        groupMembers,
        groupExpenses,
        groupExpenseSplits
    ) { members, expenses, splitsMap ->
        val balances = mutableMapOf<String, Long>()
        members.forEach { balances[it.id] = 0L }

        for (expense in expenses) {
            balances[expense.payerId] = (balances[expense.payerId] ?: 0L) + expense.amount
            
            val splits = splitsMap[expense.id] ?: emptyList()
            for (split in splits) {
                balances[split.personId] = (balances[split.personId] ?: 0L) - split.amount
            }
        }

        balances.map { PersonBalance(it.key, it.value) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settlements: StateFlow<List<Settlement>> = groupBalances
        .map { balances ->
            SettlementMinimizer.minimize(balances)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupsWithMembers: StateFlow<List<GroupWithMembers>> = allGroups
        .flatMapLatest { groups ->
            if (groups.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flows = groups.map { group ->
                    combine(
                        repository.getPersonsForGroup(group.id),
                        repository.getExpensesForGroup(group.id),
                        repository.getAllSplitsForGroup(group.id)
                    ) { members, expenses, splits ->
                        val youId = members.firstOrNull()?.id ?: ""
                        val balances = mutableMapOf<String, Long>()
                        members.forEach { balances[it.id] = 0L }

                        for (expense in expenses) {
                            balances[expense.payerId] = (balances[expense.payerId] ?: 0L) + expense.amount
                            val expenseSplits = splits.filter { it.expenseId == expense.id }
                            for (split in expenseSplits) {
                                balances[split.personId] = (balances[split.personId] ?: 0L) - split.amount
                            }
                        }
                        val yourBalance = balances[youId] ?: 0L
                        GroupWithMembers(group, members, yourBalance)
                    }
                }
                combine(flows) { it.toList() }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalOwedToYou: StateFlow<Long> = combine(
        allGroups, groupMembers, groupBalances
    ) { groups, members, balances ->
        if (groups.isNotEmpty() && members.isNotEmpty()) {
            val youId = members.first().id
            val yourBalance = balances.find { it.personId == youId }?.netBalance ?: 0L
            if (yourBalance > 0) yourBalance else 0L
        } else {
            0L
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalYouOwe: StateFlow<Long> = combine(
        allGroups, groupMembers, groupBalances
    ) { groups, members, balances ->
        if (groups.isNotEmpty() && members.isNotEmpty()) {
            val youId = members.first().id
            val yourBalance = balances.find { it.personId == youId }?.netBalance ?: 0L
            if (yourBalance < 0) -yourBalance else 0L
        } else {
            0L
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)


    fun selectGroup(groupId: String) {
        selectedGroupId.value = groupId
    }

    fun createGroup(name: String, currency: String, iconName: String, coverTheme: String, memberNames: List<String>) {
        viewModelScope.launch {
            val groupId = UUID.randomUUID().toString()
            val group = Group(groupId, name, currency, iconName, coverTheme)
            repository.insertGroup(group)

            val persons = memberNames.map {
                Person(UUID.randomUUID().toString(), groupId, it)
            }
            repository.insertPersons(persons)
        }
    }

    fun addExpense(
        title: String,
        amountCents: Long,
        category: String,
        payerId: String,
        splitMode: SplitMode,
        participantIds: List<String>,
        splitInputs: Map<String, Double>
    ) {
        viewModelScope.launch {
            val groupId = selectedGroupId.value ?: return@launch
            val expenseId = UUID.randomUUID().toString()
            val expense = Expense(
                id = expenseId,
                groupId = groupId,
                title = title,
                amount = amountCents,
                date = System.currentTimeMillis(),
                category = category,
                payerId = payerId
            )
            val splits = SplitMath.calculateSplits(amountCents, participantIds, splitMode, splitInputs).map {
                ExpenseSplit(expenseId, it.key, it.value)
            }
            repository.insertExpenseWithSplits(expense, splits)
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            val expense = groupExpenses.value.find { it.id == expenseId }
            if (expense != null) {
                repository.deleteExpense(expense)
            }
        }
    }

    fun markSettled(fromId: String, toId: String, amount: Long) {
        viewModelScope.launch {
            val groupId = selectedGroupId.value ?: return@launch
            val expenseId = UUID.randomUUID().toString()
            val expense = Expense(
                id = expenseId,
                groupId = groupId,
                title = "Settlement",
                amount = amount,
                date = System.currentTimeMillis(),
                category = "Settlement",
                payerId = fromId
            )
            val split = ExpenseSplit(expenseId, toId, amount)
            repository.insertExpenseWithSplits(expense, listOf(split))
        }
    }

    fun seedDemoData() {
        viewModelScope.launch {
            val groupId = "demo_trip"
            val group = Group(groupId, "Trip to Paris", "$", "flight", "blue")
            repository.insertGroup(group)

            val you = Person("demo_p1", groupId, "You")
            val rafi = Person("demo_p2", groupId, "Rafi")
            val anika = Person("demo_p3", groupId, "Anika")
            val sam = Person("demo_p4", groupId, "Sam")
            
            repository.insertPersons(listOf(you, rafi, anika, sam))

            val t = System.currentTimeMillis()
            
            val e1 = Expense("demo_e1", groupId, "Flights", 120000, t - 10000000, "Transport", rafi.id)
            val s1 = listOf(
                ExpenseSplit(e1.id, you.id, 30000),
                ExpenseSplit(e1.id, rafi.id, 30000),
                ExpenseSplit(e1.id, anika.id, 30000),
                ExpenseSplit(e1.id, sam.id, 30000)
            )
            repository.insertExpenseWithSplits(e1, s1)

            val e2 = Expense("demo_e2", groupId, "Dinner", 8500, t - 5000000, "Food", you.id)
            val s2 = listOf(
                ExpenseSplit(e2.id, you.id, 2500),
                ExpenseSplit(e2.id, rafi.id, 3000),
                ExpenseSplit(e2.id, anika.id, 3000)
            )
            repository.insertExpenseWithSplits(e2, s2)
        }
    }

    fun clearDemoData() {
        viewModelScope.launch {
            val group = allGroups.value.find { it.id == "demo_trip" }
            if (group != null) {
                repository.deleteGroup(group)
            }
        }
    }

    fun getGroupSummaryText(groupId: String): String {
        val group = allGroups.value.find { it.id == groupId } ?: return ""
        return "Group Summary for ${group.name}"
    }

    fun exportBackupJson(): String {
        return "{}" // TODO proper serialization
    }

    fun importBackupJson(json: String) {
        // TODO proper deserialization
    }

    class Factory(
        private val application: Application,
        private val repository: TabsplitRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
