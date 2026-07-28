package com.threesa.billing.presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
) {

    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

