package com.money.tabsplit.data.local.dao

import androidx.room.*
import com.money.tabsplit.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TabsplitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Delete
    suspend fun deleteGroup(group: GroupEntity)

    @Query("SELECT * FROM groups")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    fun getGroupById(groupId: String): Flow<GroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersons(persons: List<PersonEntity>)

    @Query("SELECT * FROM persons WHERE groupId = :groupId")
    fun getPersonsForGroup(groupId: String): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE id = :personId")
    suspend fun getPersonById(personId: String): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY date DESC")
    fun getExpensesForGroup(groupId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    fun getExpenseById(expenseId: String): Flow<ExpenseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseSplits(splits: List<ExpenseSplitEntity>)

    @Query("DELETE FROM expense_splits WHERE expenseId = :expenseId")
    suspend fun deleteSplitsForExpense(expenseId: String)

    @Query("SELECT * FROM expense_splits WHERE expenseId = :expenseId")
    fun getSplitsForExpense(expenseId: String): Flow<List<ExpenseSplitEntity>>

    @Query("SELECT * FROM expense_splits WHERE expenseId IN (SELECT id FROM expenses WHERE groupId = :groupId)")
    fun getAllSplitsForGroup(groupId: String): Flow<List<ExpenseSplitEntity>>

    @Transaction
    suspend fun insertExpenseWithSplits(expense: ExpenseEntity, splits: List<ExpenseSplitEntity>) {
        insertExpense(expense)
        deleteSplitsForExpense(expense.id)
        insertExpenseSplits(splits)
    }

    @Transaction
    suspend fun deleteExpenseWithSplits(expense: ExpenseEntity) {
        deleteSplitsForExpense(expense.id)
        deleteExpense(expense)
    }
}
