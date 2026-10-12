package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object AddEdit : Screen("add_edit?transactionId={transactionId}") {
        fun createRoute(transactionId: Long = -1L) = "add_edit?transactionId=$transactionId"
    }
    object History : Screen("history")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
