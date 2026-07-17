package com.threesa.billing.domain.model

data class PettyCashData(
    val storeName: String,
    val currentCash: Double,
    val outflows: List<PettyCashTransaction>
)