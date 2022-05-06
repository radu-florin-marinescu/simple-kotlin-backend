package com.radumarinescu

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.radumarinescu.Constants.DATABASE_URL
import com.radumarinescu.Constants.JWT_EMAIL
import com.radumarinescu.Constants.JWT_PASSWORD
import com.radumarinescu.components.users.User
import com.radumarinescu.components.users.UserRepository
import com.radumarinescu.components.users.UserRepositoryImpl
import com.radumarinescu.components.users.userRoutes
import components.messages.MessageRepository
import components.messages.MessageRepositoryImpl
import components.messages.messageRoutes
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import org.bson.UuidRepresentation
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

lateinit var secret: String
lateinit var issuer: String
lateinit var algorithm: Algorithm
lateinit var verifier: JWTVerifier
var validity: Long = 0

fun main(args: Array<String>) {
    startKoin {
        modules(module {
            single<MessageRepository> { MessageRepositoryImpl() }
            single<UserRepository> { UserRepositoryImpl() }
            single {
                KMongo
                    .createClient(
                        MongoClientSettings
                            .builder()
                            .applyConnectionString(ConnectionString(DATABASE_URL))
                            .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                            .build()
                    )
                    .coroutine
            }
        })

    }
    embeddedServer(Jetty, commandLineEnvironment(args)).start()
}

@Suppress("unused")
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(DataConversion)

    secret = environment.config.propertyOrNull("ktor.deployment.secret")?.getString()
        ?: throw Exception("Secret key is invalid")
    issuer =
        environment.config.propertyOrNull("ktor.deployment.issuer")?.getString() ?: throw Exception("Issuer is invalid")
    validity = environment.config.propertyOrNull("ktor.deployment.validity")?.getString()?.toLongOrNull() ?: 86400000
    algorithm = Algorithm.HMAC512(secret)
    verifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    install(Authentication) {
        val repository by inject<UserRepository>(UserRepository::class.java)
        jwt {
            verifier(verifier)
            realm = issuer
            validate {
                val email = it.payload.getClaim(JWT_EMAIL).asString()
                val user = repository.fetch(email)
                if (user != null) {
                    user
                } else {
                    this.respond(HttpStatusCode.Unauthorized)
                    null
                }
            }
        }
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        userRoutes()
        authenticate {
            messageRoutes()
        }
    }
}