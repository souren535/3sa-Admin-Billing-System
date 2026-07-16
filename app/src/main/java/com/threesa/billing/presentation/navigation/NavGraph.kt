package com.threesa.billing.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.threesa.billing.presentation.dashboard.DashboardScreen
import com.threesa.billing.presentation.login.LoginScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.hierarchy?.firstOrNull()?.route

    val showBottomBar = currentRoute != Screen.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) RoundedBottomNavBar(navController)
        }
    ) {
        padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(
                top = padding.calculateTopPadding()
            )
        ) {
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginScreen = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(route = Screen.Dashboard.route) {
                DashboardScreen()
            }
            composable(route = Screen.PettyCash.route) {
                Screen.PettyCash
            }
            composable(Screen.Inventory.route) {
                // placeholder until next step
            }
            composable(Screen.Reports.route) {
                // placeholder until next step
            }

        }
    }


}
