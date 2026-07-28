package com.threesa.billing.data.repository

import com.threesa.billing.data.remote.api.DashboardApi
import com.threesa.billing.domain.model.DashboardSummary
import com.threesa.billing.domain.model.Store
import com.threesa.billing.domain.model.StoreStatus
import com.threesa.billing.domain.repository.DashboardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val api: DashboardApi
) : DashboardRepository {

    override suspend fun getDashboardSummary(): Result<DashboardSummary> {
        return try {
            coroutineScope {
                val globalStatsDeferred = async { api.getGlobalStats() }
                val storeStatsDeferred = async { api.getAllStoreStats() }

                val globalStatsResponse = globalStatsDeferred.await()
                val storeStatsResponse = storeStatsDeferred.await()

                if (globalStatsResponse.success && storeStatsResponse.success) {
                    val globalData = globalStatsResponse.data
                        ?: throw Exception(globalStatsResponse.message ?: "Global stats null")
                    val storeDataList = storeStatsResponse.data ?: emptyList()

                    val stores = storeDataList.map { dto ->
                        Store(
                            id = dto.store?.id?.toString() ?: "",
                            name = dto.store?.name ?: "Unknown Store",
                            status = try {
                                StoreStatus.valueOf(dto.status?.uppercase() ?: "ACTIVE")
                            } catch (e: Exception) {
                                StoreStatus.ACTIVE
                            },
                            billsToday = dto.billsToday ?: 0,
                            totalRevenue = dto.totalRevenue ?: 0.0
                        )
                    }

                    val summary = DashboardSummary(
                        totalStores = globalData.total_stores ?: 0,
                        totalRevenue = globalData.total_revenue ?: 0.0,
                        pettyCash = globalData.petty_cash ?: 0.0,
                        stores = stores
                    )
                    Result.success(summary)
                } else {
                    Result.failure(Exception(globalStatsResponse.message ?: storeStatsResponse.message ?: "Dashboard API error"))
                }
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error (${e.code()})"))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
