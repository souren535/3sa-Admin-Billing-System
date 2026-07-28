package com.threesa.billing.data.repository

import android.util.Log
import com.threesa.billing.data.remote.api.UtilsApi
import com.threesa.billing.data.remote.dto.StoreDto
import com.threesa.billing.domain.repository.UtilsRepository
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject

class UtilsRepositoryImpl @Inject constructor(
    private val api: UtilsApi
) : UtilsRepository {
    override suspend fun getStores(): Result<List<StoreDto>> {
        return try {
            val response = api.getStores()
            Log.d("UtilsRepository", "getStores success: ${response.success}, data: ${response.data}")
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch stores"))
            }
        } catch (e: HttpException) {
            Log.e("UtilsRepository", "getStores HttpException: ${e.code()}", e)
            Result.failure(Exception("Server error (${e.code()})"))
        } catch (e: IOException) {
            Log.e("UtilsRepository", "getStores IOException", e)
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Log.e("UtilsRepository", "getStores Exception", e)
            Result.failure(e)
        }
    }
}
