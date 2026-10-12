package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.HisabViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: HisabViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val transactions by viewModel.allTransactions.collectAsState()
                val totalIncome by viewModel.totalIncome.collectAsState()
                val totalExpense by viewModel.totalExpense.collectAsState()
                val balance by viewModel.balance.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Welcome.route
                ) {
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onGetStarted = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Home.route) {
                        HomeScreen(
                            transactions = transactions,
                            totalIncome = totalIncome,
                            totalExpense = totalExpense,
                            balance = balance,
                            onAddTransaction = { type ->
                                navController.navigate(Screen.AddEdit.createRoute())
                            },
                            onNavigateToHistory = {
                                navController.navigate(Screen.History.route)
                            },
                            onNavigateToReports = {
                                navController.navigate(Screen.Reports.route)
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.Settings.route)
                            },
                            onDeleteTransaction = { tx ->
                                viewModel.deleteTransaction(tx)
                            }
                        )
                    }

                    composable(Screen.AddEdit.route) {
                        AddEditTransactionScreen(
                            initialType = "EXPENSE",
                            onSave = { title, amount, type, category, date, note ->
                                viewModel.addTransaction(title, amount, type, category, date, note)
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.History.route) {
                        HistoryScreen(
                            transactions = transactions,
                            onDeleteTransaction = { tx ->
                                viewModel.deleteTransaction(tx)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Reports.route) {
                        ReportsScreen(
                            transactions = transactions,
                            totalIncome = totalIncome,
                            totalExpense = totalExpense,
                            balance = balance,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { viewModel.toggleTheme() },
                            onClearAllData = { viewModel.clearAllData() },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
