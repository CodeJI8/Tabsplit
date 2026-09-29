package com.money.tabsplit.domain.model

data class Expense(
    val id: String,
    val groupId: String,
    val title: String,
    val amount: Long,
    val date: Long,
    val category: String,
    val payerId: String
)
