package com.threesa.billing.data.repository

import com.threesa.billing.data.remote.api.InventoryApi
import com.threesa.billing.data.remote.dto.ApiResponse
import com.threesa.billing.data.remote.dto.ProductDto
import com.threesa.billing.domain.model.InventoryData
import com.threesa.billing.domain.model.Product
import com.threesa.billing.domain.model.StockStatus
import com.threesa.billing.domain.repository.InventoryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val api: InventoryApi
) : InventoryRepository {

    override suspend fun getStats(storeId: String): Result<InventoryData> {
        return try {
            val response = api.getStats(storeId.toIntOrNull() ?: 0)
            val data = response.data
            if (response.success && data != null) {
                Result.success(
                    InventoryData(
                        storeName = data.store?.name ?: "",
                        totalProducts = data.total_products ?: 0,
                        totalStock = data.total_stocks ?: 0,
                        lowStockCount = data.low_stocks ?: 0,
                        outOfStockCount = data.out_of_stocks ?: 0
                    )
                )
            } else {
                Result.failure(Exception(response.message ?: "Failed to load inventory stats"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(mapHttpError(e.code())))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInventory(storeId: String): Result<InventoryData> = coroutineScope {
        val statsDeferred = async { getStats(storeId) }
        val productsDeferred = async { getProducts(storeId.toIntOrNull() ?: 0) }

        val statsResult = statsDeferred.await()
        val productsResult = productsDeferred.await()

        if (statsResult.isSuccess || productsResult.isSuccess) {
            val stats = statsResult.getOrDefault(InventoryData())
            val products = productsResult.getOrDefault(emptyList())
            Result.success(stats.copy(products = products))
        } else {
            Result.failure(
                statsResult.exceptionOrNull() ?: productsResult.exceptionOrNull()
                ?: Exception("Failed to load inventory")
            )
        }
    }

    override suspend fun getProducts(
        storeId: Int, categoryId: Int?, sortBy: String?, sortOrder: String?
    ): Result<List<Product>> = fetchProducts { api.getProducts(storeId, categoryId, sortBy, sortOrder) }

    override suspend fun getLowStockProducts(
        storeId: Int, categoryId: Int?, sortBy: String?, sortOrder: String?
    ): Result<List<Product>> = fetchProducts { api.getLowStock(storeId, categoryId, sortBy, sortOrder) }

    private suspend fun fetchProducts(
        call: suspend () -> ApiResponse<List<ProductDto>>
    ): Result<List<Product>> {
        return try {
            val response = call()
            val data = response.data
            if (response.success && data != null) {
                Result.success(data.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message ?: "Failed to load products"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(mapHttpError(e.code())))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ProductDto.toDomain(): Product {
        val currentStock = stock ?: 0
        val minLevel = min_stock_level ?: 0
        val domainStatus = when (status?.lowercase()) {
            "out_of_stock" -> StockStatus.OUT_OF_STOCK
            "low_stock" -> StockStatus.LOW_STOCK
            "in_stock" -> StockStatus.IN_STOCK
            else -> when {
                currentStock <= 0 -> StockStatus.OUT_OF_STOCK
                currentStock <= minLevel -> StockStatus.LOW_STOCK
                else -> StockStatus.IN_STOCK
            }
        }
        val displayName = productName ?: "Unnamed Product"
        return Product(
            id = (id ?: 0).toString(),
            name = displayName,
            category = category ?: "Uncategorized",
            stock = currentStock,
            status = domainStatus
        )
    }

    private fun mapHttpError(code: Int): String = when (code) {
        401 -> "Session expired. Please log in again."
        404 -> "Store not found"
        else -> "Server error ($code)"
    }
}