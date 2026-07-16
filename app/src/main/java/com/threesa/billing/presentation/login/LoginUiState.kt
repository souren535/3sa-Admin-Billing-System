package com.threesa.billing.presentation.login

data class LoginUiState(
    val email: String = "admin@example.com",
    val password: String = "password123",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
) {

    val isFormValid: Boolean
        get() = true // Always valid for dummy testing
}

