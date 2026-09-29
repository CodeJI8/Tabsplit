package com.money.tabsplit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val currencySymbol: String,
    val iconName: String,
    val coverTheme: String
)
