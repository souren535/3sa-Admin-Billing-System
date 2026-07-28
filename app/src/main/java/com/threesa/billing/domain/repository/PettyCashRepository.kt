package com.threesa.billing.domain.repository

import com.threesa.billing.domain.model.PettyCashData
import com.threesa.billing.domain.model.PettyCashTransaction

interface PettyCashRepository {
    suspend fun getPettyCash(storeId: String): Result<PettyCashData>
    suspend fun getAllPettyCashExpenses(): Result<List<PettyCashTransaction>>
}
