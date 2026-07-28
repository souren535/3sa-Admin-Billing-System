package com.threesa.billing.domain.usecase

import com.threesa.billing.domain.model.PettyCashData
import com.threesa.billing.domain.repository.PettyCashRepository
import javax.inject.Inject

class GetPettyCashUseCase @Inject constructor(
    private val repository: PettyCashRepository
) {
    suspend operator fun invoke(storeId: String): Result<PettyCashData> =
        repository.getPettyCash(storeId)
}
