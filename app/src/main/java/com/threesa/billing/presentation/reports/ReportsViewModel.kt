package com.threesa.billing.presentation.reports

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.repository.ReportsRepository
import com.threesa.billing.domain.repository.UtilsRepository
import com.threesa.billing.domain.usecase.GetReportsUseCase
import com.threesa.billing.utils.PdfDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getReportsUseCase: GetReportsUseCase,
    private val reportsRepository: ReportsRepository,
    private val utilsRepository: UtilsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState

    private val _downloadingInvoiceId = MutableStateFlow<String?>(null)
    val downloadingInvoiceId: StateFlow<String?> = _downloadingInvoiceId

    init {
        loadStores()
        observeSelectedStore()
    }

    private fun observeSelectedStore() {
        viewModelScope.launch {
            utilsRepository.selectedStoreId.collect { storeId ->
                if (!storeId.isNullOrBlank() && storeId != _uiState.value.selectedStoreId) {
                    _uiState.update { it.copy(selectedStoreId = storeId) }
                    loadReports(storeId)
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
                        loadReports(globalStoreId)
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

    fun loadReports(storeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getReportsUseCase(storeId).fold(
                onSuccess = { data ->
                    _uiState.update { it.copy(isLoading = false, data = data) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun exportPdf(context: Context, storeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            reportsRepository.exportPdf(storeId).fold(
                onSuccess = { dto ->
                    _uiState.update { it.copy(isLoading = false) }
                    val base64 = dto.base64_data
                    if (!base64.isNullOrBlank()) {
                        val fileName = dto.file_name ?: "Sales_Report_$storeId.pdf"
                        val success = PdfDownloader.saveBase64PdfToDownloads(context, fileName, base64)
                        if (success) {
                            _uiState.update {
                                it.copy(
                                    dialogState = ReportsDialogState(
                                        isSuccess = true,
                                        title = "Report Downloaded",
                                        message = "Sales report '$fileName' saved successfully to device Downloads folder."
                                    )
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    dialogState = ReportsDialogState(
                                        isSuccess = false,
                                        title = "Download Failed",
                                        message = "Could not save report PDF to storage."
                                    )
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                dialogState = ReportsDialogState(
                                    isSuccess = false,
                                    title = "Download Failed",
                                    message = "Report PDF data is empty."
                                )
                            )
                        }
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dialogState = ReportsDialogState(
                                isSuccess = false,
                                title = "Download Failed",
                                message = e.message ?: "Failed to generate sales report PDF."
                            )
                        )
                    }
                }
            )
        }
    }

    fun printInvoice(context: Context, invoiceId: String) {
        viewModelScope.launch {
            _downloadingInvoiceId.value = invoiceId
            reportsRepository.printInvoice(invoiceId).fold(
                onSuccess = { dto ->
                    _downloadingInvoiceId.value = null
                    val base64 = dto.base64_data
                    if (!base64.isNullOrBlank()) {
                        val fileName = dto.file_name ?: "Invoice_$invoiceId.pdf"
                        val success = PdfDownloader.saveBase64PdfToDownloads(context, fileName, base64)
                        if (success) {
                            _uiState.update {
                                it.copy(
                                    dialogState = ReportsDialogState(
                                        isSuccess = true,
                                        title = "Invoice Downloaded",
                                        message = "Invoice '$fileName' saved successfully to device Downloads folder."
                                    )
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    dialogState = ReportsDialogState(
                                        isSuccess = false,
                                        title = "Download Failed",
                                        message = "Could not save invoice PDF to device storage."
                                    )
                                )
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                dialogState = ReportsDialogState(
                                    isSuccess = false,
                                    title = "Download Failed",
                                    message = "Invoice PDF data is empty."
                                )
                            )
                        }
                    }
                },
                onFailure = { e ->
                    _downloadingInvoiceId.value = null
                    _uiState.update {
                        it.copy(
                            dialogState = ReportsDialogState(
                                isSuccess = false,
                                title = "Download Failed",
                                message = e.message ?: "Failed to download invoice PDF."
                            )
                        )
                    }
                }
            )
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = null) }
    }

    fun onStoreSelected(storeId: String) {
        utilsRepository.setSelectedStoreId(storeId)
        _uiState.update { it.copy(selectedStoreId = storeId) }
        loadReports(storeId)
    }

    fun onTabSelect(tab: ReportsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onDateSelected(date: String?) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun filteredInvoices() = _uiState.value.data?.invoices.orEmpty().filter { invoice ->
        val matchesTab = when (_uiState.value.selectedTab) {
            ReportsTab.ALL -> true
            ReportsTab.PAID -> invoice.status == com.threesa.billing.domain.model.InvoiceStatus.PAID
            ReportsTab.UNPAID -> invoice.status == com.threesa.billing.domain.model.InvoiceStatus.UNPAID
        }
        val query = _uiState.value.searchQuery.trim().lowercase()
        val matchesSearch = query.isEmpty() ||
                invoice.id.lowercase().contains(query) ||
                invoice.customerName.lowercase().contains(query) ||
                invoice.customerPhone.lowercase().contains(query) ||
                (invoice.paymentMethod?.lowercase()?.contains(query) == true)

        val selectedDate = _uiState.value.selectedDate
        val matchesDate = selectedDate.isNullOrBlank() ||
                invoice.date.contains(selectedDate) ||
                matchesDateFlexible(invoice.date, selectedDate)

        matchesTab && matchesSearch && matchesDate
    }

    private fun matchesDateFlexible(invoiceDate: String, targetDate: String): Boolean {
        if (invoiceDate.isBlank() || targetDate.isBlank()) return false
        val cleanInv = invoiceDate.replace("/", "-")
        val cleanTarget = targetDate.replace("/", "-")
        return cleanInv.contains(cleanTarget)
    }
}
