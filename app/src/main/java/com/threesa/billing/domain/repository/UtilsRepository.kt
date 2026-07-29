package com.threesa.billing.domain.repository

import com.threesa.billing.data.remote.dto.StoreDto
import kotlinx.coroutines.flow.StateFlow

interface UtilsRepository {
    val selectedStoreId: StateFlow<String?>
    fun setSelectedStoreId(storeId: String)
    suspend fun getStores(): Result<List<StoreDto>>
}
