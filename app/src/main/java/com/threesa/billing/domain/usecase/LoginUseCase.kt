package com.threesa.billing.domain.usecase

import com.threesa.billing.domain.model.Admin
import com.threesa.billing.domain.repository.AuthRepository
import javax.inject.Inject


class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(email: String, password: String): Result<Admin> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email cannot be empty"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        return repository.login(email.trim(), password)
    }
}