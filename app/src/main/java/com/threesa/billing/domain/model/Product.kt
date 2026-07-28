package com.threesa.billing.domain.model

enum class StockStatus { IN_STOCK, LOW_STOCK, OUT_OF_STOCK }

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val stock: Int,
    val status: StockStatus
)

data class InventoryData(
    val storeName: String = "",
    val totalProducts: Int = 0,
    val totalStock: Int = 0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val products: List<Product> = emptyList()
)