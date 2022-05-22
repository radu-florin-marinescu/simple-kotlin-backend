package com.radumarinescu.components.users

import com.fasterxml.jackson.core.util.RequestPayload
import com.radumarinescu.components.common.GlobalResponse
import com.radumarinescu.encryptPassword
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

// TODO Use a similar pattern in all the application.
suspend fun ApplicationCall.validateForgotPasswordConfirmationRequest(
    payload: ForgotPasswordRequest,
    repository: UserRepository,
    callback: suspend () -> Unit
) {
    val encryptedPassword = encryptPassword(payload.password ?: "")
    when {
        payload.password.isNullOrEmpty() -> {
            this.respond(
                HttpStatusCode.BadRequest, GlobalResponse<Nothing>(
                    message = "Password must not NULL or empty!",
                    success = false
                )
            )
        }

        payload.email.isNullOrEmpty() -> {
            this.respond(
                HttpStatusCode.BadRequest, GlobalResponse<Nothing>(
                    message = "Email address must not NULL or empty!",
                    success = false
                )
            )
        }

        payload.code == null || payload.code < 10000 || payload.code > 99999 -> {
            this.respond(
                HttpStatusCode.BadRequest, GlobalResponse<Nothing>(
                    message = "Confirmation code must not NULL or empty, and must be a value between 10000 and 99999!",
                    success = false
                )
            )
        }

        repository.checkPassword(LoginRequest(email = payload.email, password = encryptedPassword)) -> {
            this.respond(
                HttpStatusCode.BadRequest, GlobalResponse<Nothing>(
                    message = "New password is the same as old password. Please set up a different password!",
                    success = false
                )
            )
        }

        repository.checkIfUserExists(payload.email) -> {
            this.respond(
                HttpStatusCode.NotFound, GlobalResponse<Nothing>(
                    message = "User not found! Please add the email address of an existing account!",
                    success = false
                )
            )
        }

        payload.code != repository.getUserPendingCode(payload.email) -> {
            this.respond(
                HttpStatusCode.BadRequest, GlobalResponse<Nothing>(
                    message = "Confirmation code is invalid. Please use the code we sent to your email address!",
                    success = false
                )
            )
        }

        else -> {
            callback.invoke()
        }
    }
}