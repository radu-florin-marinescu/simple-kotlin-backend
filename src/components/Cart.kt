package com.radumarinescu.components

import java.util.*

data class Cart(
    val products: Product,
    val placedAt: Date,
    val lastEditedAt: Date,
    val courier: Courier?,
    val voucher: Voucher?
)