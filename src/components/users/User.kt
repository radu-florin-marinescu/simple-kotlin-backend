package com.radumarinescu.components.users

import com.fasterxml.jackson.annotation.JsonInclude
import io.ktor.server.auth.*
import java.util.*

data class User(
    var id: UUID,
    val email: String,
    val name: String,
    val age: Int,
    var password: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var pendingConfirmationCode: Long? = null
) : Principal

data class UserResponse(
    val email: String,
    val name: String,
    val age: Int,
)

data class LoginRequest(
    val email: String,
    var password: String
)

data class EmailRequest(
    val email: String,
)

data class ForgotPasswordRequest(
    val email: String?,
    val password: String?,
    val code: Long?
)

data class TokenResponse(
    val token: String
)