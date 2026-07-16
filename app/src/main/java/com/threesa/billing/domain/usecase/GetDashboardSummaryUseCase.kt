package com.threesa.billing.domain.usecase

import com.threesa.billing.domain.model.DashboardSummary
import com.threesa.billing.domain.repository.DashboardRepository
import javax.inject.Inject


class GetDashboardSummaryUseCase @Inject constructor(
    private val repository: DashboardRepository
){

    suspend operator fun invoke(): Result<DashboardSummary> = repository.getDashboardSummary()
}