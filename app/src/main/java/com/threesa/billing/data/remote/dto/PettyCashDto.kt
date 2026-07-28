package com.threesa.billing.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PettyCashBalanceDto(
    val store: StoreBasicDto?,
    @SerializedName("petty_cash_balance")
    val pettyCashBalance: Double?
)

data class ExpenseDto(
    val id: Int?,
    val category: String?,
    val amount: Double?,
    val notes: String?,
    @SerializedName("transaction_date")
    val transactionDate: String?,
    @SerializedName("shop_id")
    val shopId: Int?,
    @SerializedName("created_at")
    val createdAt: String?
)
