package com.threesa.billing.domain.model

enum class InvoiceStatus {
    PAID, UNPAID
}

data class Invoice(
    val id: String,
    val date: String,
    val time: String,
    val customerName: String,
    val customerPhone: String,
    val total: Double,
    val status: InvoiceStatus,
    val paymentMethod: String? = null
)
