package com.threesa.billing.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReportStatsDto(
    val store: StoreBasicDto? = null,
    @SerializedName("total_invoices", alternate = ["total_bills", "invoices_count"])
    val total_invoices: Int? = null,
    @SerializedName("total_sales", alternate = ["total_revenue", "sales_total"])
    val total_sales: Double? = null,
    @SerializedName("paid", alternate = ["paid_count", "paid_invoices"])
    val paid: Int? = null,
    @SerializedName("unpaid", alternate = ["unpaid_count", "unpaid_invoices"])
    val unpaid: Int? = null
)

data class InvoiceDto(
    val id: Int? = null,
    @SerializedName("invoice_number", alternate = ["invoice_no", "bill_no", "id_str"])
    val invoice_number: String? = null,
    val date: String? = null,
    val time: String? = null,
    @SerializedName("created_at", alternate = ["date_time"])
    val createdAt: String? = null,
    @SerializedName("customer_name", alternate = ["customer", "client_name"])
    val customer_name: String? = null,
    @SerializedName("mobile_number", alternate = ["customer_phone", "phone_number", "phone", "mobile"])
    val mobile_number: String? = null,
    @SerializedName("total_amount", alternate = ["total", "amount", "grand_total"])
    val total_amount: Double? = null,
    val paid_amount: Double? = null,
    val due_amount: Double? = null,
    val status: String? = null,
    @SerializedName("payment_method", alternate = ["payment_type", "mode"])
    val payment_method: String? = null
)

data class PdfExportDto(
    @SerializedName("file_name", alternate = ["filename"])
    val file_name: String? = null,
    @SerializedName("mime_type")
    val mime_type: String? = null,
    @SerializedName("base64_data", alternate = ["base64"])
    val base64_data: String? = null
)