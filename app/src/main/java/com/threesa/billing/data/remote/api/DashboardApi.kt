package com.threesa.billing.data.remote.api

import com.threesa.billing.data.remote.dto.ApiResponse
import com.threesa.billing.data.remote.dto.GlobalDashboardDto
import com.threesa.billing.data.remote.dto.StoreStatDto
import retrofit2.http.GET
import retrofit2.http.Path

interface DashboardApi {
    @GET("api/v1/dashboard")
    suspend fun getGlobalStats(): ApiResponse<GlobalDashboardDto>

    @GET("api/v1/dashboard/store")
    suspend fun getAllStoreStats(): ApiResponse<List<StoreStatDto>>

    @GET("api/v1/dashboard/store/{id}")
    suspend fun getStoreStats(@Path("id") storeId: Int): ApiResponse<StoreStatDto>
}