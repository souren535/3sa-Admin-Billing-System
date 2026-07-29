package com.threesa.billing.presentation.dashboard


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.repository.UtilsRepository
import com.threesa.billing.domain.usecase.GetDashboardSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase,
    private val utilsRepository: UtilsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboard()
        observeSelectedStore()
    }

    private fun observeSelectedStore() {
        viewModelScope.launch {
            utilsRepository.selectedStoreId.collect { storeId ->
                if (!storeId.isNullOrBlank()) {
                    _uiState.update { it.copy(expandedStoreId = storeId) }
                }
            }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getDashboardSummaryUseCase()
            result.fold(
                onSuccess = { summary ->
                    val globalStoreId = utilsRepository.selectedStoreId.value ?: summary.stores.firstOrNull()?.id
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            summary = summary,
                            expandedStoreId = globalStoreId
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun onStoreClick(storeId: String) {
        val newExpanded = if (_uiState.value.expandedStoreId == storeId) null else storeId
        _uiState.update { it.copy(expandedStoreId = newExpanded) }
        if (newExpanded != null) {
            utilsRepository.setSelectedStoreId(newExpanded)
        }
    }
}