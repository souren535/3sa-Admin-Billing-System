package com.threesa.billing.domain.repository

import com.threesa.billing.domain.model.ReportsData

interface ReportsRepository {
    suspend fun getReports(): Result<ReportsData>
}
