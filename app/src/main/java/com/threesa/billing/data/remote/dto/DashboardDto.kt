package com.threesa.billing.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GlobalDashboardDto(
    val total_stores: Int?,
    val total_revenue: Double?,
    val petty_cash: Double?
)

// Matches API: {"store":{"id":1,"name":"Main Store"},"total_revenue":40390.28,"bill_count":10,"store_status":"active"}
data class StoreStatDto(
    val store: StoreBasicDto?,
    @SerializedName("total_revenue")
    val totalRevenue: Double?,
    @SerializedName("bill_count")
    val billsToday: Int?,
    @SerializedName("store_status")
    val status: String?
)

data class StoreBasicDto(
    val id: Int?,
    val name: String?
)