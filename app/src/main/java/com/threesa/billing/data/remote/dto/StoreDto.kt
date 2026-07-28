package com.threesa.billing.data.remote.dto

data class StoreDto(
    val id: Int?,
    val name: String?,
    val status: String?,
    val location: String? = null
)

data class CategoryDto(
    val id: Int?,
    val name: String?
)
