package com.radumarinescu

import com.auth0.jwt.JWT
import com.radumarinescu.Constants.JWT_EMAIL
import com.radumarinescu.Constants.JWT_SCOPE
import com.radumarinescu.components.users.LoginRequest
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.util.*
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun generateToken(user: LoginRequest): String = JWT.create()
    .withSubject(JWT_SCOPE)
    .withIssuer(issuer)
    .withClaim(JWT_EMAIL, user.email)
    .withExpiresAt(Date(System.currentTimeMillis() + validity))
    .sign(algorithm)


suspend fun ApplicationCall.bearerAuthentication() {
    val authHeader = this.request.parseAuthorizationHeader()
    if (authHeader?.authScheme == "Bearer" && authHeader is HttpAuthHeader.Single) {
        try {
            verifier.verify(authHeader.blob)
        } catch (e: Exception) {
            this.respondText("error")
        }
    }
}

val hmacKey = SecretKeySpec(secret.toByteArray(), "HmacSHA1")

fun encryptPassword(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}