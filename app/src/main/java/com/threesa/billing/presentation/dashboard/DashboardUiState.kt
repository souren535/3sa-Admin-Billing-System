package com.threesa.billing.presentation.dashboard

import com.threesa.billing.domain.model.DashboardSummary

data class DashboardUiState(
    val isLoading: Boolean = true,
    val summary: DashboardSummary? = null,
    val expandedStoreId: String? = null,
    val errorMessage: String? = null
)


