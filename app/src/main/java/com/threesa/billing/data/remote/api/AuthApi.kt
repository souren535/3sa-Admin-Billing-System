package com.threesa.billing.data.remote.api

import com.threesa.billing.data.remote.dto.ApiResponse
import com.threesa.billing.data.remote.dto.ChangePasswordRequestDto
import com.threesa.billing.data.remote.dto.ForgotPasswordRequestDto
import com.threesa.billing.data.remote.dto.LoginRequestDto
import com.threesa.billing.data.remote.dto.LoginResponseDto
import com.threesa.billing.data.remote.dto.LogoutRequestDto
import com.threesa.billing.data.remote.dto.RefreshRequestDto
import com.threesa.billing.data.remote.dto.RefreshResponseDto
import com.threesa.billing.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/login")
    suspend fun login(@Body request: LoginRequestDto): ApiResponse<LoginResponseDto>

    @POST("api/v1/refresh")
    suspend fun refresh(@Body request: RefreshRequestDto): ApiResponse<RefreshResponseDto>

    @POST("api/v1/logout")
    suspend fun logout(@Body request: LogoutRequestDto): ApiResponse<Unit>

    @POST("api/v1/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): ApiResponse<Unit>

    @GET("api/v1/profile")
    suspend fun getProfile(): ApiResponse<UserDto>

    @GET("api/v1/permissions")
    suspend fun getPermissions(): ApiResponse<com.threesa.billing.data.remote.dto.PermissionsResponseDto>

    @POST("api/v1/users")
    suspend fun createUser(@Body request: com.threesa.billing.data.remote.dto.CreateUserRequestDto): retrofit2.Response<okhttp3.ResponseBody>

    @POST("api/v1/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): ApiResponse<Unit>
}
