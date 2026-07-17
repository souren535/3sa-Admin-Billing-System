package com.threesa.billing.domain.repository

import com.threesa.billing.domain.model.PettyCashData

interface PettyCashRepository {
    suspend fun getPettyCash(storeId: String): Result<PettyCashData>
}