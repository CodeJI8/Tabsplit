package com.money.tabsplit.domain.repository

import com.money.tabsplit.domain.model.*
import kotlinx.coroutines.flow.Flow

interface TabsplitRepository {
    fun getAllGroups(): Flow<List<Group>>
    fun getGroupById(groupId: String): Flow<Group?>
    suspend fun insertGroup(group: Group)
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(group: Group)

    fun getPersonsForGroup(groupId: String): Flow<List<Person>>
    suspend fun getPersonById(personId: String): Person?
    suspend fun insertPerson(person: Person)
    suspend fun insertPersons(persons: List<Person>)

    fun getExpensesForGroup(groupId: String): Flow<List<Expense>>
    fun getExpenseById(expenseId: String): Flow<Expense?>
    suspend fun insertExpenseWithSplits(expense: Expense, splits: List<ExpenseSplit>)
    suspend fun updateExpenseWithSplits(expense: Expense, splits: List<ExpenseSplit>)
    suspend fun deleteExpense(expense: Expense)

    fun getSplitsForExpense(expenseId: String): Flow<List<ExpenseSplit>>
    fun getAllSplitsForGroup(groupId: String): Flow<List<ExpenseSplit>>
}
