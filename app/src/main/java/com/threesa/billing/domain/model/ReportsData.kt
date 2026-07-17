package com.threesa.billing.domain.model

data class ReportsData(
    val storeName: String,
    val totalInvoices: Int,
    val totalSales: Double,
    val paidCount: Int,
    val unpaidCount: Int,
    val invoices: List<Invoice>
)
