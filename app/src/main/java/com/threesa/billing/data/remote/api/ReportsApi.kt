package com.threesa.billing.data.remote.api

import com.threesa.billing.data.remote.dto.ApiResponse
import com.threesa.billing.data.remote.dto.InvoiceDto
import com.threesa.billing.data.remote.dto.PdfExportDto
import com.threesa.billing.data.remote.dto.ReportStatsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportsApi {
    @GET("api/v1/reports/stats/{storeId}")
    suspend fun getStats(@Path("storeId") storeId: Int): ApiResponse<ReportStatsDto>

    @GET("api/v1/reports/invoices/{storeId}")
    suspend fun getInvoices(
        @Path("storeId") storeId: Int,
        @Query("status") status: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): ApiResponse<List<InvoiceDto>>

    @GET("api/v1/reports/export/pdf/{storeId}")
    suspend fun exportPdf(@Path("storeId") storeId: Int): ApiResponse<PdfExportDto>

    @GET("api/v1/invoices/{invoiceId}/print")
    suspend fun printInvoice(@Path("invoiceId") invoiceId: String): ApiResponse<PdfExportDto>
}