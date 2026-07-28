package com.threesa.billing.presentation.reports.pdf

import android.content.Context
import android.widget.Toast
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

data class ReportPdfUiState(
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val pdfData: PdfExportDto? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ReportPdfViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val storeId: String = savedStateHandle.get<String>("storeId") ?: "1"

    private val _uiState = MutableStateFlow(ReportPdfUiState())
    val uiState: StateFlow<ReportPdfUiState> = _uiState

    init {
        loadReportPdf()
    }

    fun loadReportPdf() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            reportsRepository.exportPdf(storeId).fold(
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
            Toast.makeText(context, "PDF data is empty", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true) }
            val fileName = pdfData.file_name ?: "sales_report_$storeId.pdf"
            val success = PdfDownloader.saveBase64PdfToDownloads(context, fileName, base64)
            _uiState.update { it.copy(isDownloading = false) }

            if (success) {
                Toast.makeText(context, "Downloaded $fileName to Downloads folder", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to download PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
