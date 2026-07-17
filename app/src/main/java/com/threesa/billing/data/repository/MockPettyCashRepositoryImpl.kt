package com.threesa.billing.data.repository

import com.threesa.billing.domain.model.OutflowIconType
import com.threesa.billing.domain.model.PettyCashData
import com.threesa.billing.domain.model.PettyCashTransaction
import com.threesa.billing.domain.repository.PettyCashRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockPettyCashRepositoryImpl @Inject constructor() : PettyCashRepository {
    override suspend fun getPettyCash(storeId: String): Result<PettyCashData> {
        delay(500)
        return Result.success(
            PettyCashData(
                storeName = "Store Alpha",
                currentCash = 15400.00,
                outflows = listOf(
                    PettyCashTransaction(
                        id = "1",
                        title = "Cleaning Supplies",
                        amount = 450.00,
                        time = "02:30 PM",
                        reason = "Emergency floor cleaning after spill in Aisle 4",
                        iconType = OutflowIconType.CLEANING
                    ),
                    PettyCashTransaction(
                        id = "2",
                        title = "Local Vendor Delivery",
                        amount = 1200.00,
                        time = "11:15 AM",
                        reason = "Cash payment for urgent fresh produce delivery",
                        iconType = OutflowIconType.DELIVERY
                    ),
                    PettyCashTransaction(
                        id = "3",
                        title = "Office Supplies",
                        amount = 350.00,
                        time = "09:45 AM",
                        reason = "Reimbursement for printer ink cartridges",
                        iconType = OutflowIconType.OFFICE
                    )
                )
            )
        )
    }
}