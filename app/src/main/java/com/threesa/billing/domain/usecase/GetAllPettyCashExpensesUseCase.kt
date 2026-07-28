package com.threesa.billing.domain.usecase

import com.threesa.billing.domain.model.PettyCashTransaction
import com.threesa.billing.domain.repository.PettyCashRepository
import javax.inject.Inject

class GetAllPettyCashExpensesUseCase @Inject constructor(
    private val repository: PettyCashRepository
) {
    suspend operator fun invoke(): Result<List<PettyCashTransaction>> =
        repository.getAllPettyCashExpenses()
}
