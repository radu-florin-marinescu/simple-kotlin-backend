package com.radumarinescu

import com.auth0.jwt.JWT
import com.radumarinescu.Constants.JWT_EMAIL
import com.radumarinescu.Constants.JWT_SCOPE
import com.radumarinescu.components.couriers.CourierRepository
import com.radumarinescu.components.users.Address
import com.radumarinescu.components.users.LoginRequest
import com.radumarinescu.components.users.User
import com.radumarinescu.components.users.UserRepository
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

suspend fun runLaunchScript() {

    try {
        val dataProducer = DataProducer()
        val courierRepository by inject<CourierRepository>(CourierRepository::class.java)

        if (!courierRepository.areCouriersAdded()) {
            courierRepository.insertAll(dataProducer.generateCourierData())
        }
    } catch (_: Exception) {
    }
}