package com.threesa.billing.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.model.Invoice
import com.threesa.billing.domain.model.InvoiceStatus
import com.threesa.billing.domain.usecase.GetReportsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReportsUseCase: GetReportsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getReportsUseCase().fold(
                onSuccess = { data ->
                    _uiState.update { it.copy(isLoading = false, data = data) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun onTabSelect(tab: ReportsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun filteredInvoices(): List<Invoice> {
        val currentData = _uiState.value.data ?: return emptyList()
        return when (_uiState.value.selectedTab) {
            ReportsTab.ALL -> currentData.invoices
            ReportsTab.PAID -> currentData.invoices.filter { it.status == InvoiceStatus.PAID }
            ReportsTab.UNPAID -> currentData.invoices.filter { it.status == InvoiceStatus.UNPAID }
        }
    }
}
