package com.threesa.billing.presentation.pettycash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.repository.UtilsRepository
import com.threesa.billing.domain.usecase.GetPettyCashUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PettyCashViewModel @Inject constructor(
    private val getPettyCashUseCase: GetPettyCashUseCase,
    private val utilsRepository: UtilsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PettyCashUiState())
    val uiState: StateFlow<PettyCashUiState> = _uiState

    init {
        loadStores()
    }

    private fun loadStores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            utilsRepository.getStores().fold(
                onSuccess = { stores ->
                    Log.d("PettyCashViewModel", "Loaded ${stores.size} stores")
                    val firstStoreId = stores.firstOrNull()?.id?.toString()
                    _uiState.update { it.copy(stores = stores, selectedStoreId = firstStoreId) }
                    if (firstStoreId != null) {
                        loadPettyCash(firstStoreId)
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "No stores found") }
                    }
                },
                onFailure = { e ->
                    Log.e("PettyCashViewModel", "Failed to load stores", e)
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun onStoreSelected(storeId: String) {
        _uiState.update { it.copy(selectedStoreId = storeId) }
        loadPettyCash(storeId)
    }

    fun loadPettyCash(storeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPettyCashUseCase(storeId).fold(
                onSuccess = { data ->
                    _uiState.update { it.copy(isLoading = false, data = data) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun refresh() {
        if (_uiState.value.stores.isEmpty()) {
            loadStores()
        } else {
            _uiState.value.selectedStoreId?.let { loadPettyCash(it) }
        }
    }
}
