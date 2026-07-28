package com.threesa.billing.data.remote.api

import com.threesa.billing.data.remote.dto.ApiResponse
import com.threesa.billing.data.remote.dto.ExpenseDto
import com.threesa.billing.data.remote.dto.PettyCashBalanceDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PettyCashApi {
    @GET("api/v1/petty-cash/{id}")
    suspend fun getCurrentCash(@Path("id") storeId: Int): ApiResponse<PettyCashBalanceDto>

    @GET("api/v1/petty-cash/{id}/expenses")
    suspend fun getStoreExpenses(@Path("id") storeId: Int): ApiResponse<List<ExpenseDto>>

    @GET("api/v1/petty-cash/expenses")
    suspend fun getAllExpenses(): ApiResponse<List<ExpenseDto>>
}