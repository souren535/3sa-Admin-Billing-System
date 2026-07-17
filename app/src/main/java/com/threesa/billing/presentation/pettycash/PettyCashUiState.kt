package com.threesa.billing.presentation.pettycash

import com.threesa.billing.domain.model.PettyCashData

data class PettyCashUiState(
    val isLoading: Boolean = true,
    val data: PettyCashData? = null,
    val errorMessage: String? = null
)