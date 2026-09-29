package com.money.tabsplit

import android.app.Application
import com.money.tabsplit.data.local.TabsplitDatabase
import com.money.tabsplit.data.repository.TabsplitRepositoryImpl
import com.money.tabsplit.domain.repository.TabsplitRepository

class TabsplitApplication : Application() {
    lateinit var repository: TabsplitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        val database = TabsplitDatabase.getDatabase(this)
        repository = TabsplitRepositoryImpl(database.tabsplitDao())
    }
}
