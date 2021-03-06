package com.radumarinescu

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.internal.Timeout
import com.radumarinescu.Constants.DATABASE_URL
import com.radumarinescu.Constants.JWT_EMAIL
import com.radumarinescu.components.common.GlobalResponse
import com.radumarinescu.components.couriers.CourierRepository
import com.radumarinescu.components.couriers.CourierRepositoryImpl
import com.radumarinescu.components.couriers.courierRoutes
import com.radumarinescu.components.dashdoard.DashboardRepository
import com.radumarinescu.components.dashdoard.DashboardRepositoryImpl
import com.radumarinescu.components.dashdoard.dashboardRoutes
import com.radumarinescu.components.users.*
import components.messages.MessageRepository
import components.messages.MessageRepositoryImpl
import components.messages.messageRoutes
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import org.bson.UuidRepresentation
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.util.*

lateinit var secret: String
lateinit var issuer: String
lateinit var algorithm: Algorithm
lateinit var verifier: JWTVerifier
var validity: Long = 0

fun main(args: Array<String>) {
    startKoin {
        modules(module {
            single<MessageRepository> { MessageRepositoryImpl() }
            single<UserRepository> { UserRepositoryImpl(get()) }
            single<DashboardRepository> { DashboardRepositoryImpl(get()) }
            single<CourierRepository> { CourierRepositoryImpl(get()) }
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
    install(DataConversion) {
        convert<CardType> {
            encode { cardType ->
                listOf(cardType.name.lowercase(Locale.getDefault()))
            }
            decode { list ->
                CardType.values().firstOrNull { it.name.lowercase(Locale.getDefault()) == (list.firstOrNull()?.lowercase(Locale.getDefault()) ?: "") } ?: CardType.UNDEFINED
            }
        }
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    HttpClient(io.ktor.client.engine.jetty.Jetty) {
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
        }
    }
    install(StatusPages) {
        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(
                HttpStatusCode.Unauthorized, GlobalResponse<Nothing>(
                    success = false,
                    message = "Access is unauthorized"
                )
            )
        }
    }

    install(Authentication) {
        val repository by inject<UserRepository>(UserRepository::class.java)

        secret = this@module.environment.config.propertyOrNull("ktor.deployment.secret")?.getString()
            ?: throw Exception("Secret key is invalid")
        issuer =
            this@module.environment.config.propertyOrNull("ktor.deployment.issuer")?.getString()
                ?: throw Exception("Issuer is invalid")
        validity =
            this@module.environment.config.propertyOrNull("ktor.deployment.validity")?.getString()?.toLongOrNull()
                ?: 86400000
        algorithm = Algorithm.HMAC512(secret)
        verifier = JWT
            .require(algorithm)
            .withIssuer(issuer)
            .build()

        jwt {
            verifier(verifier)
            realm = issuer
            validate {
                val email = it.payload.getClaim(JWT_EMAIL).asString()
                val user = repository.fetchUserWithEmail(email)
                if (user != null) {
                    user
                } else {
                    this.respond(HttpStatusCode.Unauthorized)
                    null
                }
            }
        }
    }

    routing {
        runBlocking {
            runLaunchScript()
        }

        userRoutes()
        messageRoutes()
        dashboardRoutes()
        courierRoutes()
    }
}