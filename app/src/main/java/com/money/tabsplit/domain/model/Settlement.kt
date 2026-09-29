package com.money.tabsplit.domain.model

data class Settlement(
    val fromPersonId: String,
    val toPersonId: String,
    val amount: Long
)
