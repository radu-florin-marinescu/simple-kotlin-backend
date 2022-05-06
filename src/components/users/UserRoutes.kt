package com.radumarinescu.components.users

import com.radumarinescu.bearerAuthentication
import com.radumarinescu.encryptPassword
import com.radumarinescu.generateToken
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject

fun Route.userRoutes() {

    val repository by inject<UserRepository>(UserRepository::class.java)

    route("register") {
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

            repository.insert(user)
            call.respond(HttpStatusCode.OK, "User created")
        }
    }

    route("/login") {
        put {
            val credentials = call.receive<LoginRequest>()
            credentials.password = encryptPassword(credentials.password)
            val userExists = repository.checkIfUserExists(credentials.email)
            val passwordsMatch = repository.checkPassword(credentials)
            if (!userExists) {
                call.respondText(
                    text = "User does not exist",
                    status = HttpStatusCode.NotAcceptable
                )
                return@put
            }

            if(!passwordsMatch){
                call.respondText(
                    text = "Passwords do not match",
                    status = HttpStatusCode.NotAcceptable
                )
                return@put
            }

            val token = generateToken(credentials)
            call.respond(HttpStatusCode.OK, hashMapOf("token" to token))
        }
    }

    route("/check") {
        authenticate {
            get {
                call.bearerAuthentication()
                call.principal<User>()?.let{
                    println(it.email)
                }
                call.respond(HttpStatusCode.OK, "succccc")
            }
        }
    }
}