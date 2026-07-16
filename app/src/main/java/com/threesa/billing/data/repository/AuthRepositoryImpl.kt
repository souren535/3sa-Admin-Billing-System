package com.threesa.billing.data.repository

import com.threesa.billing.data.local.datastore.SessionManager
import com.threesa.billing.data.remote.api.AuthApi
import com.threesa.billing.data.remote.dto.LoginRequestDto
import com.threesa.billing.domain.model.Admin
import com.threesa.billing.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Admin> {
        return try {
            // Dummy success for now as requested
            val dummyAdmin = Admin(
                id = "dummy_id",
                name = "Dummy Admin",
                email = email,
                token = "dummy_token"
            )
            sessionManager.saveSession(dummyAdmin.token, dummyAdmin.name)
            Result.success(dummyAdmin)
            
            /* Real implementation commented out
            val response = api.login(LoginRequestDto(email, password))
            sessionManager.saveSession(response.token, response.name)
            Result.success(
                Admin(
                    id = response.id,
                    name = response.name,
                    email = response.email,
                    token = response.token
                )
            )
            */
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun logout() {
        sessionManager.clearSession()
    }
    override suspend fun isLoggedIn(): Boolean {
        return sessionManager.tokenFlow.first() != null
    }
}
