package com.threesa.billing.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.data.local.datastore.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionManager: SessionManager
): ViewModel() {
    
    // We use a StateFlow with an initial 'null' value to represent "checking state"
    // Then it transitions to 'true' or 'false'.
    val isLoggedIn: StateFlow<Boolean?> = sessionManager.tokenFlow
        .map { !it.isNullOrBlank() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
}
