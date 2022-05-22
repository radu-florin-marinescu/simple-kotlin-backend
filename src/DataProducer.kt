package com.radumarinescu

import com.radumarinescu.components.couriers.Courier
import java.util.*

class DataProducer {

    fun generateCourierData(): List<Courier> {
        return listOf(
            Courier(
                id = UUID.fromString("9326a318-0845-4767-8460-cecec3c201be"),
                iconURL = "",
                name = "Sameday",
                deliveryFee = 18.00,
                estimatedDeliveryDays = 2
            ),

            Courier(
                id = UUID.fromString("208f1fd8-f029-4b43-8bcc-f4c696e927e5"),
                iconURL = "",
                name = "Fan Courier",
                deliveryFee = 22.00,
                estimatedDeliveryDays = 3
            ),
            Courier(
                id = UUID.fromString("28678e48-0c12-4cf8-a425-a2524c915d6b"),
                iconURL = "",
                name = "DPD",
                deliveryFee = 10.00,
                estimatedDeliveryDays = 8
            ),
            Courier(
                id = UUID.fromString("93f2c1fe-d7e7-4fe0-a355-03aab59049e2"),
                iconURL = "",
                name = "Cargus",
                deliveryFee = 19.99,
                estimatedDeliveryDays = 4
            )
        )
    }
}