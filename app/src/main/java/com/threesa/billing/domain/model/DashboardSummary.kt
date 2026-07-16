package com.threesa.billing.domain.model

data class DashboardSummary(
    val totalStores: Int,
    val totalRevenue: Double,
    val pettyCash: Double,
    val stores: List<Store>
)

