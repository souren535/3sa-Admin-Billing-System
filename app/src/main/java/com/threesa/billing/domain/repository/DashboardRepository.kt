package com.threesa.billing.domain.repository

import com.threesa.billing.domain.model.DashboardSummary

interface DashboardRepository {
    suspend fun getDashboardSummary(): Result<DashboardSummary>
}