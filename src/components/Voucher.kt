package com.radumarinescu.components

import java.util.*

data class Voucher(
    val id: UUID?,
    val name: String,
    val discount: Double,
    val minimumOrder: Double
)