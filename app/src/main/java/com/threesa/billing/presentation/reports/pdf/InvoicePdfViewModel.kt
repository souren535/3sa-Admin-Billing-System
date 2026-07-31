package com.threesa.billing.presentation.reports.pdf

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.data.remote.dto.PdfExportDto
import com.threesa.billing.domain.repository.ReportsRepository
import com.threesa.billing.utils.PdfDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvoicePdfDialogState(
    val isSuccess: Boolean,
    val title: String,
    val message: String
)

data class InvoicePdfUiState(
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val pdfData: PdfExportDto? = null,
    val errorMessage: String? = null,
    val dialogState: InvoicePdfDialogState? = null
)

@HiltViewModel
class InvoicePdfViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val invoiceId: String = savedStateHandle.get<String>("invoiceId") ?: ""

    private val _uiState = MutableStateFlow(InvoicePdfUiState())
    val uiState: StateFlow<InvoicePdfUiState> = _uiState

    init {
        if (invoiceId.isNotBlank()) {
            loadInvoicePdf()
        } else {
            _uiState.update { it.copy(errorMessage = "Invalid Invoice ID") }
        }
    }

    fun loadInvoicePdf() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            reportsRepository.printInvoice(invoiceId).fold(
                onSuccess = { dto ->
                    _uiState.update { it.copy(isLoading = false, pdfData = dto) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun downloadPdfToDevice(context: Context) {
        val pdfData = _uiState.value.pdfData ?: return
        val base64 = pdfData.base64_data
        if (base64.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    dialogState = InvoicePdfDialogState(
                        isSuccess = false,
                        title = "Download Failed",
                        message = "PDF data is empty."
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true) }
            val fileName = pdfData.file_name ?: "invoice_$invoiceId.pdf"
            val success = PdfDownloader.saveBase64PdfToDownloads(context, fileName, base64)
            _uiState.update { it.copy(isDownloading = false) }

            if (success) {
                _uiState.update {
                    it.copy(
                        dialogState = InvoicePdfDialogState(
                            isSuccess = true,
                            title = "Invoice Downloaded",
                            message = "Invoice '$fileName' saved successfully to device Downloads folder."
                        )
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        dialogState = InvoicePdfDialogState(
                            isSuccess = false,
                            title = "Download Failed",
                            message = "Could not save invoice PDF to device storage."
                        )
                    )
                }
            }
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = null) }
    }
}
