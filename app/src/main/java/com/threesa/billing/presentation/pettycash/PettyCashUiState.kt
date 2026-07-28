package com.threesa.billing.presentation.pettycash

import com.threesa.billing.data.remote.dto.StoreDto
import com.threesa.billing.domain.model.PettyCashData

data class PettyCashUiState(
    val isLoading: Boolean = false,
    val data: PettyCashData? = null,
    val stores: List<StoreDto> = emptyList(),
    val selectedStoreId: String? = null,
    val errorMessage: String? = null
)