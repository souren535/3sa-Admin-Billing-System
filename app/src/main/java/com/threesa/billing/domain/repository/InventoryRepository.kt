package com.threesa.billing.domain.repository

import com.threesa.billing.domain.model.InventoryData

interface InventoryRepository {
    suspend fun getInventory(storeId: String): Result<InventoryData>
}