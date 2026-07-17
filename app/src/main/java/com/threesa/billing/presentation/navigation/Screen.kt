package com.threesa.billing.presentation.navigation

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Dashboard: Screen("dashboard")
    object PettyCash : Screen("petty_cash")
    object Inventory : Screen("inventory")
    object Reports : Screen("reports")
    object Profile : Screen("profile")
}