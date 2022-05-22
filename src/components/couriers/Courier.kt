package com.radumarinescu.components.couriers

import java.util.*

data class Courier(
    val id: UUID,
    val iconURL: String,
    val name: String,
    val deliveryFee: Double,
    val estimatedDeliveryDays: Int
)
