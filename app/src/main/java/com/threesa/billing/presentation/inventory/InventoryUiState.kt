package com.threesa.billing.presentation.inventory

import com.threesa.billing.data.remote.dto.StoreDto
import com.threesa.billing.domain.model.InventoryData

enum class InventoryTab { ALL, LOW_STOCK }

data class InventoryUiState(
    val isLoading: Boolean = false,
    val data: InventoryData? = null,
    val stores: List<StoreDto> = emptyList(),
    val categories: List<String> = listOf("All Categories"),
    val selectedStoreId: String? = null,
    val selectedCategory: String = "All Categories",
    val selectedTab: InventoryTab = InventoryTab.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null
)