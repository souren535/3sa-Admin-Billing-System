package com.threesa.billing.data.repository

import android.util.Log
import com.threesa.billing.data.local.datastore.SessionManager
import com.threesa.billing.data.remote.api.AuthApi
import com.threesa.billing.data.remote.dto.LoginRequestDto
import com.threesa.billing.data.remote.dto.LogoutRequestDto
import com.threesa.billing.data.remote.dto.RefreshRequestDto
import com.threesa.billing.domain.model.Admin
import com.threesa.billing.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {
    private val refreshMutex = Mutex()

    override suspend fun login(email: String, password: String): Result<Admin> {
        return try {
            val response = api.login(LoginRequestDto(email, password))
            val data = response.data
            if (response.success && data != null) {
                sessionManager.saveSession(
                    token = data.access_token,
                    refreshToken = data.refresh_token,
                    name = data.user?.name ?: "Admin"
                )
                Result.success(
                    Admin(
                        id = data.user?.id?.toString() ?: "",
                        name = data.user?.name ?: "Admin",
                        email = data.user?.email ?: email,
                        token = data.access_token
                    )
                )
            } else {
                Result.failure(Exception(response.message ?: "Login failed"))
            }
        } catch (e: HttpException) {
            val errorMsg = when (e.code()) {
                401 -> "Invalid email or password"
                422 -> "Please check your input"
                else -> "Server error (${e.code()})"
            }
            Result.failure(Exception(errorMsg))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshAccessToken(): Boolean = refreshMutex.withLock {
        // We use firstOrNull to avoid hanging if the flow is empty
        val oldToken = sessionManager.tokenFlow.firstOrNull()
        val refreshToken = sessionManager.refreshTokenFlow.firstOrNull() ?: return false
        
        // Double-check if another request already refreshed the token
        val currentToken = sessionManager.tokenFlow.firstOrNull()
        if (currentToken != oldToken && !currentToken.isNullOrBlank()) {
            return true
        }

        return try {
            Log.d("AuthRepository", "Calling refresh API...")
            val response = api.refresh(RefreshRequestDto(refreshToken))
            val data = response.data
            if (response.success && data != null) {
                Log.d("AuthRepository", "Refresh API success, saving new session")
                sessionManager.saveSession(
                    token = data.access_token,
                    refreshToken = data.refresh_token,
                    name = sessionManager.adminNameFlow.firstOrNull() ?: "Admin"
                )
                true
            } else {
                Log.d("AuthRepository", "Refresh API failed: ${response.message}")
                sessionManager.clearSession()
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Refresh API exception", e)
            if (e is HttpException && (e.code() == 401 || e.code() == 403)) {
                sessionManager.clearSession()
            }
            false
        }
    }

    override suspend fun logout() {
        val refreshToken = sessionManager.refreshTokenFlow.firstOrNull()
        try {
            if (!refreshToken.isNullOrBlank()) {
                api.logout(LogoutRequestDto(refreshToken))
            }
        } catch (_: Exception) {
        } finally {
            sessionManager.clearSession()
        }
    }

    override suspend fun isLoggedIn(): Boolean = sessionManager.tokenFlow.firstOrNull() != null

    override suspend fun getProfile(): Result<com.threesa.billing.data.remote.dto.UserDto> {
        return try {
            val response = api.getProfile()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch profile"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error (${e.code()})"))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPermissions(): Result<com.threesa.billing.data.remote.dto.PermissionsResponseDto> {
        return try {
            val response = api.getPermissions()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch permissions"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Server error (${e.code()})"))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(
        name: String, email: String, password: String, role: String, shopId: Int?
    ): Result<com.threesa.billing.data.remote.dto.UserDto> {
        return try {
            val response = api.createUser(
                com.threesa.billing.data.remote.dto.CreateUserRequestDto(name, email, password, role, shopId)
            )
            val rawBody = if (response.isSuccessful) {
                response.body()?.string()
            } else {
                response.errorBody()?.string()
            }
            Log.d("AuthRepository", "createUser HTTP ${response.code()} raw response: $rawBody")

            if (rawBody.isNullOrBlank()) {
                if (response.isSuccessful) {
                    return Result.success(com.threesa.billing.data.remote.dto.UserDto(name = name, email = email, role = role))
                } else {
                    return Result.failure(Exception("Server error (${response.code()})"))
                }
            }

            val json = try {
                org.json.JSONObject(rawBody)
            } catch (_: Exception) {
                null
            }

            if (json != null) {
                val success = json.optBoolean("success", response.isSuccessful)
                val message = json.optString("message", "")
                
                // Check for validation errors object (e.g. {"errors": {"email": ["..."]}})
                val errorsObj = json.optJSONObject("errors")
                val firstError = if (errorsObj != null) {
                    val keys = errorsObj.keys()
                    if (keys.hasNext()) {
                        val key = keys.next()
                        val arr = errorsObj.optJSONArray(key)
                        if (arr != null && arr.length() > 0) arr.getString(0) else errorsObj.optString(key)
                    } else null
                } else null

                if (success && response.isSuccessful) {
                    val dataObj = json.optJSONObject("data") ?: json.optJSONObject("user")
                    val user = if (dataObj != null) {
                        com.threesa.billing.data.remote.dto.UserDto(
                            id = dataObj.optInt("id", 0).takeIf { it != 0 },
                            name = dataObj.optString("name", name),
                            email = dataObj.optString("email", email),
                            role = dataObj.optString("role", role),
                            shop_id = dataObj.optInt("shop_id", shopId ?: 0).takeIf { it != 0 }
                        )
                    } else {
                        com.threesa.billing.data.remote.dto.UserDto(name = name, email = email, role = role, shop_id = shopId)
                    }
                    Result.success(user)
                } else {
                    val rawErr = firstError ?: message.takeIf { it.isNotBlank() }
                    Result.failure(Exception(getFriendlyErrorMessage(rawErr, response.code())))
                }
            } else {
                if (response.isSuccessful) {
                    Result.success(com.threesa.billing.data.remote.dto.UserDto(name = name, email = email, role = role))
                } else {
                    Result.failure(Exception(getFriendlyErrorMessage(null, response.code())))
                }
            }
        } catch (e: IOException) {
            Log.e("AuthRepository", "createUser IO Exception", e)
            Result.failure(Exception("No internet connection. Please check your network and try again."))
        } catch (e: Exception) {
            Log.e("AuthRepository", "createUser Exception", e)
            Result.failure(Exception(getFriendlyErrorMessage(e.message, 0)))
        }
    }

    override suspend fun changePassword(
        currentPassword: String, newPassword: String, confirmPassword: String
    ): Result<Unit> {
        return try {
            val response = api.changePassword(
                com.threesa.billing.data.remote.dto.ChangePasswordRequestDto(
                    current_password = currentPassword,
                    new_password = newPassword,
                    new_password_confirmation = confirmPassword
                )
            )
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to change password"))
            }
        } catch (e: HttpException) {
            val msg = if (e.code() == 422) "Current password incorrect or confirmation mismatch." else "Server error (${e.code()})"
            Result.failure(Exception(msg))
        } catch (e: IOException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun getFriendlyErrorMessage(rawMessage: String?, statusCode: Int = 0): String {
    val msg = rawMessage?.trim() ?: ""
    if (msg.contains("email has already been taken", ignoreCase = true) || msg.contains("email taken", ignoreCase = true)) {
        return "An account with this email address already exists. Please use a different email."
    }
    if (msg.contains("email", ignoreCase = true) && msg.contains("invalid", ignoreCase = true)) {
        return "Please enter a valid email address."
    }
    if (msg.contains("password", ignoreCase = true) && (msg.contains("short", ignoreCase = true) || msg.contains("6", ignoreCase = true))) {
        return "Password must be at least 6 characters long."
    }
    if (msg.contains("Json", ignoreCase = true) || msg.contains("Reader", ignoreCase = true) || msg.contains("Malformed", ignoreCase = true) || msg.contains("path $", ignoreCase = true)) {
        return "Invalid user input or email format. Please check your entries and try again."
    }
    if (msg.isNotBlank() && !msg.contains("Json", ignoreCase = true) && !msg.contains("path $", ignoreCase = true) && !msg.contains("column", ignoreCase = true)) {
        return msg
    }
    return when (statusCode) {
        422 -> "Validation error. Please check the email format, name, and password."
        401, 403 -> "You do not have permission to perform this action."
        409 -> "An account with this email address already exists."
        500, 502, 503 -> "Server error. Please try again in a few moments."
        else -> "Failed to process request. Please check your entries and try again."
    }
}
