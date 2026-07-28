package com.threesa.billing.domain.usecase

import com.threesa.billing.domain.model.ReportsData
import com.threesa.billing.domain.repository.ReportsRepository
import javax.inject.Inject

class GetReportsUseCase @Inject constructor(
    private val repository: ReportsRepository
) {
    suspend operator fun invoke(storeId: String): Result<ReportsData> {
        return repository.getReports(storeId)
    }
}
