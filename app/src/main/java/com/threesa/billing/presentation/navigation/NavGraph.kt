package com.threesa.billing.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.threesa.billing.presentation.dashboard.DashboardScreen
import com.threesa.billing.presentation.inventory.InventoryScreen
import com.threesa.billing.presentation.login.LoginScreen
import com.threesa.billing.presentation.pettycash.HistoricalPettyCashScreen
import com.threesa.billing.presentation.pettycash.PettyCashScreen

@Composable
fun NavGraph(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by sessionViewModel.isLoggedIn.collectAsStateWithLifecycle()

    // 1. Navigation State Tracking
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.hierarchy?.firstOrNull()?.route

    // 2. UI logic
    // We only show bottom bar if we ARE on a screen and it's NOT splash/login/profile
    val showBottomBar = currentRoute != null && 
                       currentRoute != Screen.Splash.route &&
                       currentRoute != Screen.Login.route && 
                       currentRoute != Screen.Profile.route &&
                       !currentRoute.startsWith("report_pdf")

    // 3. Reactive Auto-Navigation (Only for runtime state changes after splash)
    LaunchedEffect(isLoggedIn) {
        if (currentRoute == null || currentRoute == Screen.Splash.route) return@LaunchedEffect

        if (isLoggedIn == true && currentRoute == Screen.Login.route) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else if (isLoggedIn == false && currentRoute != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Wrap everything in the Scaffold to keep layout insets stable
    Scaffold(
        bottomBar = {
            if (showBottomBar) RoundedBottomNavBar(navController)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                composable(route = Screen.Splash.route) {
                    com.threesa.billing.presentation.splash.SplashScreen(
                        isLoggedIn = isLoggedIn,
                        onSplashComplete = { loggedIn ->
                            val targetRoute = if (loggedIn) Screen.Dashboard.route else Screen.Login.route
                            navController.navigate(targetRoute) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }
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
                    DashboardScreen(
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    )
                }
                composable(route = Screen.PettyCash.route) {
                    PettyCashScreen(
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToHistorical = { navController.navigate(Screen.HistoricalPettyCash.route) }
                    )
                }
                composable(route = Screen.HistoricalPettyCash.route) {
                    HistoricalPettyCashScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.Inventory.route) {
                    InventoryScreen(
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    )
                }
                composable(Screen.Reports.route) {
                    com.threesa.billing.presentation.reports.ReportsScreen(
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onExportPdfClick = { storeId ->
                            navController.navigate(Screen.ReportPdf.createRoute(storeId))
                        }
                    )
                }
                composable(
                    route = Screen.ReportPdf.route,
                    arguments = listOf(androidx.navigation.navArgument("storeId") { type = androidx.navigation.NavType.StringType })
                ) {
                    com.threesa.billing.presentation.reports.pdf.ReportPdfScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.Profile.route) {
                    com.threesa.billing.presentation.profile.ProfileScreen(
                        onBackClick = { navController.popBackStack() },
                        onLogoutClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
