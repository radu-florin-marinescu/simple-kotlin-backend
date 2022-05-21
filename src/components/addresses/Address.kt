package com.radumarinescu.components.addresses

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Address(
    var id: UUID,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var userId: UUID?,
    val address: String,
    val placesId: String?,
    val latitude: Long,
    val longitude: Long,
)