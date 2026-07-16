package com.threesa.billing.data.repository

import com.threesa.billing.domain.model.DashboardSummary
import com.threesa.billing.domain.model.Store
import com.threesa.billing.domain.model.StoreStatus
import com.threesa.billing.domain.repository.DashboardRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockDashboardRepositoryImpl @Inject constructor() : DashboardRepository {

    override suspend fun getDashboardSummary(): Result<DashboardSummary> {
        delay(600)
        val stores = listOf(
            Store(
                id = "store_1",
                name = "Store 1",
                status = StoreStatus.ACTIVE,
                billsToday = 45,
                totalRevenue = 58400.00
            ),
            Store(
                id = "store_2",
                name = "Store 2",
                status = StoreStatus.ACTIVE,
                billsToday = 32,
                totalRevenue = 41250.00
            ),
            Store(
                id = "store_3",
                name = "Store 3",
                status = StoreStatus.RESTRICTED,
                billsToday = 18,
                totalRevenue = 22900.00
            )
        )
        return Result.success(
            DashboardSummary(
                totalStores = stores.size,
                totalRevenue = 10000.0, // matches mockup header stat
                pettyCash = 15000.0,
                stores = stores
            )
        )
    }
}