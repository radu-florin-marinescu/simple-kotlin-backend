package com.radumarinescu.components

import com.radumarinescu.components.couriers.Courier
import java.util.*

data class Cart(
    val products: Product,
    val placedAt: Date,
    val lastEditedAt: Date,
    val courier: Courier?,
    val voucher: Voucher?
)