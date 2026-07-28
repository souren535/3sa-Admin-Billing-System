package com.threesa.billing.domain.repository

import com.threesa.billing.data.remote.dto.StoreDto

interface UtilsRepository {
    suspend fun getStores(): Result<List<StoreDto>>
}
