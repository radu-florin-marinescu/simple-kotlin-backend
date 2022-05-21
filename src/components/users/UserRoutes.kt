package com.radumarinescu.components.users

import com.radumarinescu.bearerAuthentication
import com.radumarinescu.components.UserDetailsResponse
import com.radumarinescu.components.common.GlobalResponse
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

fun Route.userRoutes() {

    val repository by inject<UserRepository>(UserRepository::class.java)

    route("/auth") {
        route("/register") {
            put {
                val user = call.receive<User>()
                user.password = encryptPassword(user.password)
                val userAlreadyExists = repository.checkIfUserExists(user.email)
                if (userAlreadyExists) {
                    call.respondText(
                        text = "User already exists",
                        status = HttpStatusCode.NotAcceptable
                    )
                    return@put
                }
                user.id = UUID.randomUUID()

                call.respond(HttpStatusCode.OK, repository.insertUser(user))
            }
        }

        route("/login") {
            put {
                val credentials = call.receive<LoginRequest>()
                credentials.password = encryptPassword(credentials.password)
                val userExists = repository.checkIfUserExists(credentials.email)
                val passwordsMatch = repository.checkPassword(credentials)
                if (!userExists) {
                    call.respond(
                        HttpStatusCode.NotAcceptable,
                        GlobalResponse<Nothing>(
                            message = "User does not exist",
                            success = false
                        )
                    )
                    return@put
                }

                if (!passwordsMatch) {
                    call.respond(
                        HttpStatusCode.NotAcceptable,
                        GlobalResponse<Nothing>(
                            message = "Passwords do not match",
                            success = false
                        )
                    )
                    return@put
                }

                val token = generateToken(credentials)
                call.respond(
                    HttpStatusCode.OK,
                    GlobalResponse(
                        data = TokenResponse(token),
                        success = true
                    )
                )
            }
        }

        route("/check") {
            authenticate {
                get {
                    call.bearerAuthentication()
                    call.principal<User>()?.let {
                        delay(5000)
                        println(it.email)
                    }
                    call.respond(
                        HttpStatusCode.OK, GlobalResponse<Nothing>(
                            message = "Token is still valid",
                            success = true
                        )
                    )
                }
            }
        }
    }

    route("/me") {
        authenticate {
            get {
                onAuthenticate { call, userId ->
                    val fullUser = repository.fetchUser(userId) ?: return@onAuthenticate
                    val addresses = repository.fetchAddressesForUser(userId)

                    val response = GlobalResponse(
                        success = true,
                        data = UserDetailsResponse(
                            user = UserResponse(
                                email = fullUser.email,
                                age = fullUser.age,
                                name = fullUser.name
                            ),
                            addresses = addresses.data ?: emptyList(),
                            orderCount = 0
                        )
                    )
                    call.respond(HttpStatusCode.OK, response)
                }
            }
        }
    }

    route("/address") {
        authenticate {
            get {
                onAuthenticate { call, userId ->
                    val result = repository.fetchAddressesForUser(userId)
                    call.respond(HttpStatusCode.OK, result)
                }
            }

            put {
                onAuthenticate { call, userId ->
                    val address = call.receive<Address>()

                    if (address.id == null) {
                        val result = repository.insertAddressForUser(userId, address)
                        call.respond(HttpStatusCode.OK, result)
                    } else {
                        val result = repository.updateAddressForUser(userId, address)
                        call.respond(HttpStatusCode.OK, result)
                    }
                }
            }

            delete {
                onAuthenticate { call, userId ->
                    val queryParam = call.request.queryParameters.getOrFail("id")
                    try {
                        val result = repository.deleteAddressForUser(userId, UUID.fromString(queryParam))
                        call.respond(HttpStatusCode.OK, result)
                    } catch (e: Exception){
                        if( e is IllegalArgumentException){
                            call.respond(HttpStatusCode.BadRequest, "Id is invalid")
                        }
                    }
                }
            }
        }
    }
}