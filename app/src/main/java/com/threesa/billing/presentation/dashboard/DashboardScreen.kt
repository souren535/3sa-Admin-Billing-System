package com.threesa.billing.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import com.threesa.billing.presentation.common.components.AppHeader
import com.threesa.billing.ui.theme.BackgroundCream
import com.threesa.billing.ui.theme.TextPrimary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.ui.graphics.Color
import com.threesa.billing.ui.theme.PrimaryOrange
import com.threesa.billing.ui.theme.SuccessGreen
import com.threesa.billing.ui.theme.SuccessGreenBg
import com.threesa.billing.ui.theme.WarningAmber
import com.threesa.billing.ui.theme.WarningAmberBg
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.threesa.billing.presentation.dashboard.components.StoreExpandableCard
import com.threesa.billing.presentation.dashboard.components.SummaryCard
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToProfile: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rupeeFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
    ) {
        AppHeader(onAvatarClick = onNavigateToProfile)
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadDashboard() },
            indicator = {},
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.summary != null) {
                val summary = uiState.summary!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    item(key = "summary_cards") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SummaryCard(
                                label = "Total Stores",
                                value = "${summary.totalStores}",
                                icon = Icons.Default.Storefront,
                                iconColor = PrimaryOrange,
                                cardBgColor = Color(0xFFFFF0EB),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                label = "Total Revenue",
                                value = rupeeFormat.format(summary.totalRevenue).replace(".00", ""),
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                iconColor = SuccessGreen,
                                cardBgColor = SuccessGreenBg,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(28.dp))
                    }

                    item(key = "daily_summary_header") {
                        Text(
                            text = "Daily Summary",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    items(summary.stores, key = { "store_${it.id}" }) { store ->
                        StoreExpandableCard(
                            store = store,
                            isExpanded = uiState.expandedStoreId == store.id,
                            onClick = { viewModel.onStoreClick(store.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    item(key = "bottom_spacer") { Spacer(Modifier.height(80.dp)) }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryOrange
                )
            }

            if (uiState.errorMessage != null && uiState.summary == null) {
                Text(
                    uiState.errorMessage ?: "Something went wrong",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
