package com.threesa.billing.domain.repository

import com.threesa.billing.data.remote.dto.PdfExportDto
import com.threesa.billing.domain.model.ReportsData

interface ReportsRepository {
    suspend fun getReports(storeId: String): Result<ReportsData>
    suspend fun exportPdf(storeId: String): Result<PdfExportDto>
    suspend fun printInvoice(invoiceId: String): Result<PdfExportDto>
}
