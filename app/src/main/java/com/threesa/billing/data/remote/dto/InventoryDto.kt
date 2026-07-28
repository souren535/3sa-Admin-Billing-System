package com.threesa.billing.data.remote.dto

import com.google.gson.annotations.SerializedName

data class InventoryStatsDto(
    val store: StoreBasicDto? = null,
    val total_products: Int? = null,
    @SerializedName("total_stocks", alternate = ["total_stock"])
    val total_stocks: Int? = null,
    @SerializedName("low_stocks", alternate = ["low_stock"])
    val low_stocks: Int? = null,
    @SerializedName("out_of_stocks", alternate = ["out_of_stock"])
    val out_of_stocks: Int? = null
)


data class ProductDto(
    val id: Int? = null,
    @SerializedName("product_name", alternate = ["name"])
    val productName: String? = null,
    val category: String? = null,
    val stock: Int? = null,
    val min_stock_level: Int? = null,
    val status: String? = null,
    val purchase_price: Double? = null,
    val selling_price: Double? = null
)