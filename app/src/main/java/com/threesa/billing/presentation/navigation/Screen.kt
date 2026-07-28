package com.threesa.billing.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login: Screen("login")
    object Dashboard: Screen("dashboard")
    object PettyCash : Screen("petty_cash")
    object HistoricalPettyCash : Screen("historical_petty_cash")
    object Inventory : Screen("inventory")
    object Reports : Screen("reports")
    object ReportPdf : Screen("report_pdf/{storeId}") {
        fun createRoute(storeId: String) = "report_pdf/$storeId"
    }
    object Profile : Screen("profile")
}