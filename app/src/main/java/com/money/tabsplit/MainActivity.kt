package com.money.tabsplit

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.money.tabsplit.ui.MainViewModel
import com.money.tabsplit.ui.TabsplitNavigation
import com.money.tabsplit.ui.theme.TabsplitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as TabsplitApplication
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(app, app.repository)
            )
            
            val themeMode by viewModel.themeMode.collectAsState()
            val isDarkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            
            TabsplitTheme(darkTheme = isDarkTheme) {
                TabsplitNavigation()
            }
        }
    }
}