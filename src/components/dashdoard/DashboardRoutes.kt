package com.radumarinescu.components.dashdoard

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.java.KoinJavaComponent.inject
import java.lang.IllegalArgumentException
import java.util.*

fun Route.dashboardRoutes() {

    val repository by inject<DashboardRepository>(DashboardRepository::class.java)

    route("/dashboard") {
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
                } catch (e: Exception){
                    if( e is IllegalArgumentException){
                        call.respond(HttpStatusCode.BadRequest, "Id is invalid")
                    }
                }
            }
        }
    }
}