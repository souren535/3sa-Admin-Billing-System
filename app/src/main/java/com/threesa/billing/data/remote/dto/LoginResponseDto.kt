package com.threesa.billing.data.remote.dto

data class LoginResponseDto(
    val id: String,
    val name: String,
    val email: String,
    val token: String
)

