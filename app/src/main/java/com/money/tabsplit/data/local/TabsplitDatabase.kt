package com.money.tabsplit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.money.tabsplit.data.local.entity.*
import com.money.tabsplit.data.local.dao.TabsplitDao

@Database(
    entities = [
        GroupEntity::class,
        PersonEntity::class,
        ExpenseEntity::class,
        ExpenseSplitEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TabsplitDatabase : RoomDatabase() {
    abstract fun tabsplitDao(): TabsplitDao

    companion object {
        @Volatile
        private var INSTANCE: TabsplitDatabase? = null

        fun getDatabase(context: android.content.Context): TabsplitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    TabsplitDatabase::class.java,
                    "tabsplit_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
