package com.threesa.billing.domain.model


data class PettyCashTransaction(
    val id: String,
    val title: String,
    val amount: Double,
    val time: String,
    val reason: String,
    val iconType: OutflowIconType
)
enum class OutflowIconType {CLEANING, DELIVERY, OFFICE}