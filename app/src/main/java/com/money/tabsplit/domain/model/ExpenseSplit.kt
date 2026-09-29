package com.money.tabsplit.domain.model

data class ExpenseSplit(
    val expenseId: String,
    val personId: String,
    val amount: Long
)
