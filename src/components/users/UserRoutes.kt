package com.radumarinescu.components.users

import com.radumarinescu.bearerAuthentication
import com.radumarinescu.components.UserDetailsResponse
import com.radumarinescu.components.addresses.Address
import com.radumarinescu.components.addresses.AddressResponse
import com.radumarinescu.components.common.GlobalResponse
import com.radumarinescu.encryptPassword
import com.radumarinescu.generateToken
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import org.litote.kmongo.MongoOperator
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

    route("/address") {
        authenticate {
            get {
                call.bearerAuthentication()
                call.principal<User>()?.let { user ->
                    val result = repository.fetchAddressesForUser(user.id ?: return@get)
                    call.respond(HttpStatusCode.OK, result)
                }
            }

            put {
                call.bearerAuthentication()
                call.principal<User>()?.let { user ->
                    val address = call.receive<Address>()
                    address.userId = user.id ?: return@put
                    val result = repository.insertAddressForUser(address)
                    call.respond(HttpStatusCode.OK, result)
                }
            }
        }

        route("/me") {
            authenticate {
                get {
                    call.bearerAuthentication()
                    call.principal<User>()?.let { user ->
                        val fullUser = repository.fetchUser(user.id) ?: return@get
                        val addresses = repository.fetchAddressesForUser(user.id)

                        val response = GlobalResponse<UserDetailsResponse>(
                            success = true,
                            data = UserDetailsResponse(
                                user = UserResponse(
                                    email = fullUser.email,
                                    age = fullUser.age,
                                    name = fullUser.name
                                ),
                                addresses = addresses.data.map { address ->
                                    AddressResponse(

                                    )
                                }
                            )
                        )
                        call.respond(HttpStatusCode.OK, result)
                    }
                }
            }
        }
    }