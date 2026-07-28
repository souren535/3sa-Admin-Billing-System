package com.threesa.billing.presentation.profile

import com.threesa.billing.data.remote.dto.PermissionsResponseDto
import com.threesa.billing.data.remote.dto.StoreDto
import com.threesa.billing.data.remote.dto.UserDto

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserDto? = null,
    val permissions: PermissionsResponseDto? = null,
    val stores: List<StoreDto> = emptyList(),
    val errorMessage: String? = null,
    
    // Bottom Sheet Modal states
    val showCreateUserModal: Boolean = false,
    val isCreatingUser: Boolean = false,
    
    val showChangePasswordModal: Boolean = false,
    val isChangingPassword: Boolean = false,
    
    val successMessage: String? = null
)
