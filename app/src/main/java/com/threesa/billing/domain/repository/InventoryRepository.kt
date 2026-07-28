package com.threesa.billing.domain.repository

import com.threesa.billing.domain.model.InventoryData
import com.threesa.billing.domain.model.Product

interface InventoryRepository {
    suspend fun getStats(storeId: String): Result<InventoryData>
    suspend fun getInventory(storeId: String): Result<InventoryData>

    suspend fun getProducts(
        storeId: Int,
        categoryId: Int? = null,
        sortBy: String? = null,
        sortOrder: String? = null
    ): Result<List<Product>>

    suspend fun getLowStockProducts(
        storeId: Int,
        categoryId: Int? = null,
        sortBy: String? = null,
        sortOrder: String? = null
    ): Result<List<Product>>

}

