package com.radumarinescu.components.addresses

data class AddressResponse(
    val address: String,
    val placesId: String?,
    val latitude: Long,
    val longitude: Long,
)