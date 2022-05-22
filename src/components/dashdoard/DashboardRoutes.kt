package com.radumarinescu.components.dashdoard

import com.radumarinescu.components.common.GlobalResponse
import com.radumarinescu.components.users.EmailRequest
import com.radumarinescu.components.users.User
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.java.KoinJavaComponent.inject
import java.lang.IllegalArgumentException
import java.util.*

fun Route.dashboardRoutes() {

    val repository by inject<DashboardRepository>(DashboardRepository::class.java)

    route("/dashboard") {
        route("/user"){
            get{
                val email = call.receive<EmailRequest>()
                val user = repository.fetchUser(email.email)
                call.respond(
                    HttpStatusCode.OK, GlobalResponse<User?>(
                        data = user,
                        success = true
                    )
                )
            }
        }
        route("/address") {
            get {
                val result = repository.fetchAddresses()
                call.respond(HttpStatusCode.OK, result)
            }

            delete {
                val queryParam = call.request.queryParameters.getOrFail("id")
                try {
                    val result = repository.deleteAddress(UUID.fromString(queryParam))
                    call.respond(HttpStatusCode.OK, result)
                } catch (e: Exception) {
                    if (e is IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Id is invalid")
                    }
                }
            }
        }

        route("/checkUserPendingConfirmationCode") {
            get {
                val email = call.receive<EmailRequest>()
                val code = repository.fetchUserPendingCode(email.email)

                call.respond(
                    HttpStatusCode.OK, GlobalResponse<Nothing>(
                        message = "Code: $code",
                        success = true
                    )
                )
            }
        }
    }
}