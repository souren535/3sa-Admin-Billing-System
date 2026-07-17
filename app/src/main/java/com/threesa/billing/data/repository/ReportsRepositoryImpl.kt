package com.threesa.billing.data.repository

import com.threesa.billing.domain.model.Invoice
import com.threesa.billing.domain.model.InvoiceStatus
import com.threesa.billing.domain.model.ReportsData
import com.threesa.billing.domain.repository.ReportsRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor() : ReportsRepository {
    override suspend fun getReports(): Result<ReportsData> {
        delay(800) // Simulate network delay
        
        val invoices = listOf(
            Invoice(
                id = "FAAOWC1800",
                date = "20 May 2025",
                time = "10:15 AM",
                customerName = "Souren Khan",
                customerPhone = "9153XXXXXX",
                total = 11610.00,
                status = InvoiceStatus.PAID,
                paymentMethod = "Online"
            ),
            Invoice(
                id = "FABBL11800",
                date = "20 May 2025",
                time = "10:05 AM",
                customerName = "Taniya Mete",
                customerPhone = "8016XXXXXX",
                total = 9599.00,
                status = InvoiceStatus.PAID,
                paymentMethod = "Cash"
            ),
            Invoice(
                id = "FAAAGJ1801",
                date = "20 May 2025",
                time = "09:45 AM",
                customerName = "Souren Khan",
                customerPhone = "7585XXXXXX",
                total = 799.00,
                status = InvoiceStatus.UNPAID,
                paymentMethod = null
            )
        )
        
        return Result.success(
            ReportsData(
                storeName = "Store Alpha",
                totalInvoices = 32,
                totalSales = 22008.00,
                paidCount = 28,
                unpaidCount = 4,
                invoices = invoices
            )
        )
    }
}
