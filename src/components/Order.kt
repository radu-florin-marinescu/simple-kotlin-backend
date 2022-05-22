package com.radumarinescu.components

import com.radumarinescu.components.couriers.Courier
import java.util.*

data class Order(
    val orderId: String,
    val status: OrderStatus,
    val placedAt: Date,
    val products: List<Product>,
    val courier: Courier,
)
