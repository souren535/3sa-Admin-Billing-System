package com.threesa.billing.domain.usecase

import com.threesa.billing.domain.model.InventoryData
import com.threesa.billing.domain.repository.InventoryRepository
import javax.inject.Inject

class GetInventoryUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(storeId: String = "store_1"): Result<InventoryData> =
        repository.getInventory(storeId)
}