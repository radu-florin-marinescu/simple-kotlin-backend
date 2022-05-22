package com.radumarinescu.components.couriers

import com.radumarinescu.bearerAuthentication
import com.radumarinescu.components.UserDetailsResponse
import com.radumarinescu.components.common.GlobalResponse
import com.radumarinescu.components.users.LoginRequest
import com.radumarinescu.components.users.TokenResponse
import com.radumarinescu.components.users.UserResponse
import com.radumarinescu.encryptPassword
import com.radumarinescu.generateToken
import com.radumarinescu.onAuthenticate
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.delay
import org.koin.java.KoinJavaComponent.inject
import java.lang.IllegalArgumentException
import java.util.*

fun Route.courierRoutes() {

    val repository by inject<CourierRepository>(CourierRepository::class.java)

    route("/courier") {
        authenticate {
            get {
                onAuthenticate { call, userId ->
                    val result = repository.fetchCouriers()
                    call.respond(HttpStatusCode.OK, result)
                }
            }
        }
    }
}