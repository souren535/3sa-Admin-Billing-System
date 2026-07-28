package com.threesa.billing.presentation.pettycash

import com.threesa.billing.domain.model.PettyCashTransaction

data class HistoricalPettyCashUiState(
    val isLoading: Boolean = false,
    val expenses: List<PettyCashTransaction> = emptyList(),
    val errorMessage: String? = null
)
