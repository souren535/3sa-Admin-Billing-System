package com.threesa.billing.presentation.reports

import com.threesa.billing.data.remote.dto.StoreDto
import com.threesa.billing.domain.model.ReportsData

enum class ReportsTab {
    ALL, PAID, UNPAID
}

data class ReportsDialogState(
    val isSuccess: Boolean,
    val title: String,
    val message: String
)

data class ReportsUiState(
    val isLoading: Boolean = false,
    val data: ReportsData? = null,
    val stores: List<StoreDto> = emptyList(),
    val selectedStoreId: String? = null,
    val errorMessage: String? = null,
    val selectedTab: ReportsTab = ReportsTab.ALL,
    val searchQuery: String = "",
    val selectedDate: String? = null,
    val dialogState: ReportsDialogState? = null
)
