package com.threesa.billing.presentation.pettycash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.usecase.GetAllPettyCashExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoricalPettyCashViewModel @Inject constructor(
    private val getAllPettyCashExpensesUseCase: GetAllPettyCashExpensesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoricalPettyCashUiState())
    val uiState: StateFlow<HistoricalPettyCashUiState> = _uiState

    init {
        loadHistoricalExpenses()
    }

    fun loadHistoricalExpenses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getAllPettyCashExpensesUseCase().fold(
                onSuccess = { expenses ->
                    _uiState.update { it.copy(isLoading = false, expenses = expenses) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            )
        }
    }
}
