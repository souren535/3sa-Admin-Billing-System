package com.threesa.billing.data.remote.api

import com.threesa.billing.data.remote.dto.ApiResponse
import com.threesa.billing.data.remote.dto.CategoryDto
import com.threesa.billing.data.remote.dto.StoreDto
import com.threesa.billing.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UtilsApi {
    @GET("api/v1/stores")
    suspend fun getStores(): ApiResponse<List<StoreDto>>

    @GET("api/v1/stores/{id}")
    suspend fun getStoreDetails(@Path("id") storeId: Int): ApiResponse<StoreDto>

    @GET("api/v1/stores/{id}/categories")
    suspend fun getStoreCategories(@Path("id") storeId: Int): ApiResponse<List<CategoryDto>>

    @GET("api/v1/users")
    suspend fun getUsers(): ApiResponse<List<UserDto>>
}