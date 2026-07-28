package com.threesa.billing.data.remote.api

import com.threesa.billing.data.remote.dto.ApiResponse
import com.threesa.billing.data.remote.dto.InventoryStatsDto
import com.threesa.billing.data.remote.dto.ProductDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InventoryApi {
    @GET("api/v1/inventory/stats/{storeId}")
    suspend fun getStats(@Path("storeId") storeId: Int): ApiResponse<InventoryStatsDto>

    @GET("api/v1/inventory/{storeId}")
    suspend fun getProducts(
        @Path("storeId") storeId: Int,
        @Query("category_id") categoryId: Int? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("sort_order") sortOrder: String? = null
    ): ApiResponse<List<ProductDto>>

    @GET("api/v1/inventory/{storeId}/low-stock")
    suspend fun getLowStock(
        @Path("storeId") storeId: Int,
        @Query("category_id") categoryId: Int? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("sort_order") sortOrder: String? = null
    ): ApiResponse<List<ProductDto>>
}