package com.radumarinescu

import com.fasterxml.jackson.databind.SerializationFeature
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.radumarinescu.Constants.DATABASE_URL
import components.messages.MessageRepository
import components.messages.MessageRepositoryImpl
import components.messages.messageRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import org.bson.UuidRepresentation
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    startKoin {
        modules(module {
            single<MessageRepository> { MessageRepositoryImpl() }
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

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(DataConversion)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        messageRoutes()
    }
}