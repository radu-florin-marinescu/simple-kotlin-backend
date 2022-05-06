package com.radumarinescu.components.users

import io.ktor.server.auth.*
import java.util.*

data class User(
    val id: UUID,
    val email: String,
    val name: String,
    val age: Int,
    var password: String
) : Principal

data class LoginRequest(
    val email: String,
    var password: String
)