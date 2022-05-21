package com.radumarinescu.components.users

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Address(
    var id: UUID?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var userId: UUID?,
    val address: String,
    val placesId: String?,
    val latitude: Long,
    val longitude: Long,
)