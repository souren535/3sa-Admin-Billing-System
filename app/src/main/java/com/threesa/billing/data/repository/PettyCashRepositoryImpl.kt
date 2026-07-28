package com.threesa.billing.data.repository

import android.util.Log
import com.threesa.billing.data.remote.api.PettyCashApi
import com.threesa.billing.domain.model.OutflowIconType
import com.threesa.billing.domain.model.PettyCashData
import com.threesa.billing.domain.model.PettyCashTransaction
import com.threesa.billing.domain.repository.PettyCashRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

class PettyCashRepositoryImpl @Inject constructor(
    private val api: PettyCashApi
) : PettyCashRepository {

    override suspend fun getPettyCash(storeId: String): Result<PettyCashData> {
        Log.d("PettyCashRepo", "Fetching for storeId: $storeId")
        return try {
            val sId = storeId.toIntOrNull() ?: throw Exception("Invalid store ID: $storeId")
            coroutineScope {
                val balanceDeferred = async { api.getCurrentCash(sId) }
                val expensesDeferred = async { api.getStoreExpenses(sId) }

                val balanceResponse = balanceDeferred.await()
                val expensesResponse = expensesDeferred.await()

                Log.d("PettyCashRepo", "Balance success: ${balanceResponse.success}, data: ${balanceResponse.data}")

                if (balanceResponse.success && balanceResponse.data != null) {
                    val balanceData = balanceResponse.data
                    val expensesData = expensesResponse.data ?: emptyList()

                    val transactions = expensesData.map { dto ->
                        PettyCashTransaction(
                            id = dto.id?.toString() ?: "",
                            title = dto.category ?: "Unknown Expense",
                            amount = dto.amount ?: 0.0,
                            time = dto.transactionDate ?: "",
                            reason = dto.notes ?: "",
                            iconType = mapCategoryToIcon(dto.category)
                        )
                    }

                    Result.success(
                        PettyCashData(
                            storeName = balanceData.store?.name ?: "Unknown Store",
                            currentCash = balanceData.pettyCashBalance ?: 0.0,
                            outflows = transactions
                        )
                    )
                } else {
                    Result.failure(Exception(balanceResponse.message ?: "Failed to fetch petty cash data"))
                }
            }
        } catch (e: HttpException) {
            Log.e("PettyCashRepo", "HttpException: ${e.code()}", e)
            Result.failure(Exception("Server error (${e.code()})"))
        } catch (e: IOException) {
            Log.e("PettyCashRepo", "IOException", e)
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Log.e("PettyCashRepo", "Exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllPettyCashExpenses(): Result<List<PettyCashTransaction>> {
        return try {
            val response = api.getAllExpenses()
            if (response.success && response.data != null) {
                val transactions = response.data.map { dto ->
                    PettyCashTransaction(
                        id = dto.id?.toString() ?: "",
                        title = dto.category ?: "Unknown Expense",
                        amount = dto.amount ?: 0.0,
                        time = dto.transactionDate ?: "",
                        reason = dto.notes ?: "",
                        iconType = mapCategoryToIcon(dto.category)
                    )
                }
                Result.success(transactions)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch historical expenses"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapCategoryToIcon(category: String?): OutflowIconType {
        if (category == null) return OutflowIconType.CLEANING
        val lower = category.lowercase()
        return when {
            lower.contains("transport") || lower.contains("delivery") -> OutflowIconType.DELIVERY
            lower.contains("office") || lower.contains("supply") || lower.contains("tea") || lower.contains("snack") -> OutflowIconType.OFFICE
            else -> OutflowIconType.CLEANING
        }
    }
}
