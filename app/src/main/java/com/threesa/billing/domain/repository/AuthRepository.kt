package com.threesa.billing.domain.repository

import com.threesa.billing.data.remote.dto.PermissionsResponseDto
import com.threesa.billing.data.remote.dto.UserDto
import com.threesa.billing.domain.model.Admin

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Admin>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun refreshAccessToken(): Boolean
    suspend fun getProfile(): Result<UserDto>
    suspend fun getPermissions(): Result<PermissionsResponseDto>
    suspend fun createUser(name: String, email: String, password: String, role: String, shopId: Int?): Result<UserDto>
    suspend fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String): Result<Unit>
}

