package com.threesa.billing.domain.model

enum class StoreStatus {ACTIVE, RESTRICTED}

data class Store(
    val id : String,
    val name: String,
    val status: StoreStatus,
    val billsToday: Int,
    val totalRevenue: Double
)

