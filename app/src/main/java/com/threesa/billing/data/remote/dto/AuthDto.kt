package com.threesa.billing.data.remote.dto

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    val access_token: String,
    val refresh_token: String,
    val token_type: String? = null, // usually "Bearer" — harmless if absent
    val user: UserDto?
)

data class UserDto(
    val id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val shop_id: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

data class RefreshRequestDto(val refresh_token: String)
data class RefreshResponseDto(val access_token: String, val refresh_token: String)

data class LogoutRequestDto(val refresh_token: String)
data class ForgotPasswordRequestDto(val email: String)

data class ChangePasswordRequestDto(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)

data class CreateUserRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val shop_id: Int? = null
)

data class RolePermissionDetailDto(
    val display_name: String? = null,
    val permissions: List<String>? = emptyList()
)

data class PermissionsResponseDto(
    val roles: Map<String, RolePermissionDetailDto>? = emptyMap()
)