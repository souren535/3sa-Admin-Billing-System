package com.threesa.billing.presentation.inventory

import com.threesa.billing.domain.model.InventoryData

enum class InventoryTab { ALL, LOW_STOCK }

data class InventoryUiState(
    val isLoading: Boolean = true,
    val data: InventoryData? = null,
    val selectedTab: InventoryTab = InventoryTab.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null
)