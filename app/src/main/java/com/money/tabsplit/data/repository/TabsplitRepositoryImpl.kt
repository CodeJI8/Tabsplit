package com.money.tabsplit.data.repository

import com.money.tabsplit.data.local.dao.TabsplitDao
import com.money.tabsplit.data.local.entity.*
import com.money.tabsplit.domain.model.*
import com.money.tabsplit.domain.repository.TabsplitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TabsplitRepositoryImpl(
    private val dao: TabsplitDao
) : TabsplitRepository {

    override fun getAllGroups(): Flow<List<Group>> {
        return dao.getAllGroups().map { list -> list.map { it.toDomain() } }
    }

    override fun getGroupById(groupId: String): Flow<Group?> {
        return dao.getGroupById(groupId).map { it?.toDomain() }
    }

    override suspend fun insertGroup(group: Group) {
        dao.insertGroup(group.toEntity())
    }

    override suspend fun updateGroup(group: Group) {
        dao.updateGroup(group.toEntity())
    }

    override suspend fun deleteGroup(group: Group) {
        dao.deleteGroup(group.toEntity())
    }

    override fun getPersonsForGroup(groupId: String): Flow<List<Person>> {
        return dao.getPersonsForGroup(groupId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getPersonById(personId: String): Person? {
        return dao.getPersonById(personId)?.toDomain()
    }

    override suspend fun insertPerson(person: Person) {
        dao.insertPerson(person.toEntity())
    }

    override suspend fun insertPersons(persons: List<Person>) {
        dao.insertPersons(persons.map { it.toEntity() })
    }

    override fun getExpensesForGroup(groupId: String): Flow<List<Expense>> {
        return dao.getExpensesForGroup(groupId).map { list -> list.map { it.toDomain() } }
    }

    override fun getExpenseById(expenseId: String): Flow<Expense?> {
        return dao.getExpenseById(expenseId).map { it?.toDomain() }
    }

    override suspend fun insertExpenseWithSplits(expense: Expense, splits: List<ExpenseSplit>) {
        dao.insertExpenseWithSplits(
            expense.toEntity(),
            splits.map { it.toEntity() }
        )
    }

    override suspend fun updateExpenseWithSplits(expense: Expense, splits: List<ExpenseSplit>) {
        // In this implementation, insert is replaced by REPLACE strategy, so we can reuse the insert method
        dao.insertExpenseWithSplits(
            expense.toEntity(),
            splits.map { it.toEntity() }
        )
    }

    override suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpenseWithSplits(expense.toEntity())
    }

    override fun getSplitsForExpense(expenseId: String): Flow<List<ExpenseSplit>> {
        return dao.getSplitsForExpense(expenseId).map { list -> list.map { it.toDomain() } }
    }

    override fun getAllSplitsForGroup(groupId: String): Flow<List<ExpenseSplit>> {
        return dao.getAllSplitsForGroup(groupId).map { list -> list.map { it.toDomain() } }
    }
}

// Extension functions for mapping
internal fun GroupEntity.toDomain() = Group(id, name, currencySymbol, iconName, coverTheme)
internal fun Group.toEntity() = GroupEntity(id, name, currencySymbol, iconName, coverTheme)

internal fun PersonEntity.toDomain() = Person(id, groupId, name)
internal fun Person.toEntity() = PersonEntity(id, groupId, name)

internal fun ExpenseEntity.toDomain() = Expense(id, groupId, title, amount, date, category, payerId)
internal fun Expense.toEntity() = ExpenseEntity(id, groupId, title, amount, date, category, payerId)

internal fun ExpenseSplitEntity.toDomain() = ExpenseSplit(expenseId, personId, amount)
internal fun ExpenseSplit.toEntity() = ExpenseSplitEntity(expenseId, personId, amount)
