package com.threesa.billing.data.repository

import com.threesa.billing.domain.model.InventoryData
import com.threesa.billing.domain.model.Product
import com.threesa.billing.domain.model.StockStatus
import com.threesa.billing.domain.repository.InventoryRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockInventoryRepositoryImpl @Inject constructor() : InventoryRepository {
    override suspend fun getInventory(storeId: String): Result<InventoryData> {
        delay(500)
        val products = listOf(
            Product("1", "iPhone 15 Pro", "Mobiles", 12, StockStatus.IN_STOCK),
            Product("2", "MacBook Air M2", "Laptops", 7, StockStatus.IN_STOCK),
            Product("3", "Sony WH-1000XM5", "Headphones", 5, StockStatus.LOW_STOCK),
            Product("4", "Samsung Galaxy Watch 6", "Smart Watches", 3, StockStatus.LOW_STOCK)
        )
        return Result.success(
            InventoryData(
                storeName = "Store Alpha",
                totalProducts = 128,
                totalStock = 3456,
                lowStockCount = 18,
                outOfStockCount = 2,
                products = products
            )
        )
    }
}