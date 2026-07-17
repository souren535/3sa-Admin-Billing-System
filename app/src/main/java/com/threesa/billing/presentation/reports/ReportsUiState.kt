package com.threesa.billing.presentation.reports

import com.threesa.billing.domain.model.ReportsData

enum class ReportsTab {
    ALL, PAID, UNPAID
}

data class ReportsUiState(
    val isLoading: Boolean = false,
    val data: ReportsData? = null,
    val errorMessage: String? = null,
    val selectedTab: ReportsTab = ReportsTab.ALL
)
