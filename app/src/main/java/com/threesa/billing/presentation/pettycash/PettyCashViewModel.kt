package com.threesa.billing.presentation.pettycash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.usecase.GetPettyCashUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PettyCashViewModel @Inject constructor(
    private val getPettyCashUseCase: GetPettyCashUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PettyCashUiState())
    val uiState: StateFlow<PettyCashUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPettyCashUseCase().fold(
                onSuccess = { data -> _uiState.update { it.copy(isLoading = false, data = data) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            )
        }
    }
}