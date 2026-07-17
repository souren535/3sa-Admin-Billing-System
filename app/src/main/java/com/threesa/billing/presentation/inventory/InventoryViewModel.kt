package com.threesa.billing.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.model.StockStatus
import com.threesa.billing.domain.usecase.GetInventoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryUseCase: GetInventoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getInventoryUseCase().fold(
                onSuccess = { data -> _uiState.update { it.copy(isLoading = false, data = data) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            )
        }
    }

    fun onTabSelect(tab: InventoryTab) = _uiState.update { it.copy(selectedTab = tab) }
    fun onSearchChange(query: String) = _uiState.update { it.copy(searchQuery = query) }

    fun filteredProducts() = _uiState.value.data?.products.orEmpty()
        .filter { it.name.contains(_uiState.value.searchQuery, ignoreCase = true) }
        .filter {
            _uiState.value.selectedTab == InventoryTab.ALL || it.status == StockStatus.LOW_STOCK
        }
}