package com.threesa.billing.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threesa.billing.domain.repository.AuthRepository
import com.threesa.billing.domain.repository.UtilsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val utilsRepository: UtilsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfileAndPermissions()
    }

    fun loadProfileAndPermissions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val profileDeferred = async { authRepository.getProfile() }
            val permissionsDeferred = async { authRepository.getPermissions() }
            val storesDeferred = async { utilsRepository.getStores() }

            val profileResult = profileDeferred.await()
            val permissionsResult = permissionsDeferred.await()
            val storesResult = storesDeferred.await()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    profile = profileResult.getOrNull() ?: it.profile,
                    permissions = permissionsResult.getOrNull() ?: it.permissions,
                    stores = storesResult.getOrNull() ?: it.stores,
                    errorMessage = profileResult.exceptionOrNull()?.message ?: permissionsResult.exceptionOrNull()?.message
                )
            }
        }
    }

    fun openCreateUserModal() = _uiState.update { it.copy(showCreateUserModal = true, successMessage = null, errorMessage = null) }
    fun closeCreateUserModal() = _uiState.update { it.copy(showCreateUserModal = false) }

    fun openChangePasswordModal() = _uiState.update { it.copy(showChangePasswordModal = true, successMessage = null, errorMessage = null) }
    fun closeChangePasswordModal() = _uiState.update { it.copy(showChangePasswordModal = false) }

    fun createUser(name: String, email: String, pass: String, role: String, shopId: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingUser = true, errorMessage = null) }
            authRepository.createUser(name, email, pass, role, shopId).fold(
                onSuccess = { newUser ->
                    _uiState.update {
                        it.copy(
                            isCreatingUser = false,
                            showCreateUserModal = false,
                            successMessage = "User ${newUser.name ?: ""} created successfully!"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isCreatingUser = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun changePassword(current: String, newPass: String, confirmPass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true, errorMessage = null) }
            authRepository.changePassword(current, newPass, confirmPass).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isChangingPassword = false,
                            showChangePasswordModal = false,
                            successMessage = "Password changed successfully!"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isChangingPassword = false, errorMessage = e.message) }
                }
            )
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSuccess()
        }
    }

    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, successMessage = null) }
}
