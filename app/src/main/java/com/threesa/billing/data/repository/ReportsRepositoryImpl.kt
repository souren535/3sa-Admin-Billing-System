package com.threesa.billing.data.repository

import com.threesa.billing.data.remote.api.ReportsApi
import com.threesa.billing.data.remote.dto.InvoiceDto
import com.threesa.billing.data.remote.dto.ReportStatsDto
import com.threesa.billing.domain.model.Invoice
import com.threesa.billing.domain.model.InvoiceStatus
import com.threesa.billing.domain.model.ReportsData
import com.threesa.billing.domain.repository.ReportsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
    private val api: ReportsApi
) : ReportsRepository {

    override suspend fun getReports(storeId: String): Result<ReportsData> = coroutineScope {
        val sId = storeId.toIntOrNull() ?: 0
        val statsDeferred = async { getStats(sId) }
        val invoicesDeferred = async { getInvoices(sId) }

        val statsResult = statsDeferred.await()
        val invoicesResult = invoicesDeferred.await()

        if (statsResult.isSuccess || invoicesResult.isSuccess) {
            val stats = statsResult.getOrNull()
            val invoices = invoicesResult.getOrDefault(emptyList())

            val storeName = stats?.store?.name ?: ""
            val totalInvoices = stats?.total_invoices ?: invoices.size
            val totalSales = stats?.total_sales ?: invoices.sumOf { it.total }
            val paidCount = stats?.paid ?: invoices.count { it.status == InvoiceStatus.PAID }
            val unpaidCount = stats?.unpaid ?: invoices.count { it.status == InvoiceStatus.UNPAID }

            Result.success(
                ReportsData(
                    storeName = storeName,
                    totalInvoices = totalInvoices,
                    totalSales = totalSales,
                    paidCount = paidCount,
                    unpaidCount = unpaidCount,
                    invoices = invoices
                )
            )
        } else {
            Result.failure(
                statsResult.exceptionOrNull() ?: invoicesResult.exceptionOrNull()
                ?: Exception("Failed to load reports")
            )
        }
    }

    override suspend fun exportPdf(storeId: String): Result<com.threesa.billing.data.remote.dto.PdfExportDto> {
        return try {
            val response = api.exportPdf(storeId.toIntOrNull() ?: 0)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to generate report PDF"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(mapHttpError(e.code())))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun printInvoice(invoiceId: String): Result<com.threesa.billing.data.remote.dto.PdfExportDto> {
        return try {
            val response = api.printInvoice(invoiceId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to generate invoice PDF"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(mapHttpError(e.code())))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getStats(storeId: Int): Result<ReportStatsDto> {
        return try {
            val response = api.getStats(storeId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch report stats"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(mapHttpError(e.code())))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getInvoices(storeId: Int): Result<List<Invoice>> {
        return try {
            val response = api.getInvoices(storeId)
            if (response.success && response.data != null) {
                val invoices = response.data.map { it.toDomain() }
                Result.success(invoices)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch invoices"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(mapHttpError(e.code())))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun InvoiceDto.toDomain(): Invoice {
        val rawInvoiceId = id?.toString() ?: invoice_number ?: "0"
        val displayInvoiceId = invoice_number ?: id?.let { "INV-$it" } ?: "INV-00"
        val statusEnum = when (status?.lowercase()) {
            "unpaid", "pending", "due" -> InvoiceStatus.UNPAID
            "paid" -> InvoiceStatus.PAID
            else -> if ((due_amount ?: 0.0) <= 0.0 && (paid_amount ?: 0.0) > 0.0) InvoiceStatus.PAID else InvoiceStatus.UNPAID
        }
        val invoiceTotal = total_amount ?: 0.0

        val displayDate = date ?: createdAt?.take(10) ?: ""
        val displayTime = time ?: if ((createdAt?.length ?: 0) >= 16) createdAt?.substring(11, 16) ?: "" else ""

        return Invoice(
            id = displayInvoiceId,
            rawId = rawInvoiceId,
            date = displayDate,
            time = displayTime,
            customerName = customer_name ?: "Walk-in Customer",
            customerPhone = mobile_number ?: "",
            total = invoiceTotal,
            status = statusEnum,
            paymentMethod = payment_method
        )
    }

    private fun mapHttpError(code: Int): String = when (code) {
        401 -> "Session expired. Please log in again."
        404 -> "Store not found"
        else -> "Server error ($code)"
    }
}
