package com.threesa.billing.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.model.StockStatus
import com.threesa.billing.domain.repository.UtilsRepository
import com.threesa.billing.domain.usecase.GetInventoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getInventoryUseCase: GetInventoryUseCase,
    private val utilsRepository: UtilsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState

    init {
        loadStores()
        observeSelectedStore()
    }

    private fun observeSelectedStore() {
        viewModelScope.launch {
            utilsRepository.selectedStoreId.collect { storeId ->
                if (!storeId.isNullOrBlank() && storeId != _uiState.value.selectedStoreId) {
                    _uiState.update { it.copy(selectedStoreId = storeId) }
                    loadInventory(storeId)
                }
            }
        }
    }

    private fun loadStores() {
        viewModelScope.launch {
            utilsRepository.getStores().fold(
                onSuccess = { stores ->
                    val globalStoreId = utilsRepository.selectedStoreId.value ?: stores.firstOrNull()?.id?.toString() ?: "1"
                    _uiState.update { it.copy(stores = stores, selectedStoreId = globalStoreId) }
                    if (_uiState.value.data == null || globalStoreId != "1") {
                        loadInventory(globalStoreId)
                    }
                },
                onFailure = { e ->
                    if (_uiState.value.data == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                    }
                }
            )
        }
    }

    fun loadInventory(storeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getInventoryUseCase(storeId).fold(
                onSuccess = { data ->
                    val categories = listOf("All Categories") + data.products.map { it.category }.distinct().sorted()
                    _uiState.update { it.copy(
                        isLoading = false,
                        data = data,
                        categories = categories
                    ) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun onStoreSelected(storeId: String) {
        utilsRepository.setSelectedStoreId(storeId)
        _uiState.update { it.copy(selectedStoreId = storeId) }
        loadInventory(storeId)
    }

    fun onTabSelect(tab: InventoryTab) = _uiState.update { it.copy(selectedTab = tab) }
    fun onSearchChange(query: String) = _uiState.update { it.copy(searchQuery = query) }
    fun onCategorySelected(category: String) = _uiState.update { it.copy(selectedCategory = category) }

    fun filteredProducts() = _uiState.value.data?.products.orEmpty()
        .filter { it.name.contains(_uiState.value.searchQuery, ignoreCase = true) }
        .filter {
            _uiState.value.selectedCategory == "All Categories" || it.category == _uiState.value.selectedCategory
        }
        .filter {
            _uiState.value.selectedTab == InventoryTab.ALL || it.status != StockStatus.IN_STOCK
        }
}
