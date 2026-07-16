package com.threesa.billing.domain.repository

import com.threesa.billing.domain.model.Admin

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Admin>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}

