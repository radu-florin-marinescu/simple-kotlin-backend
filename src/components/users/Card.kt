package com.radumarinescu.components.users

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

data class Card(
    var id: UUID?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var userId: UUID?,
    val type: CardType,
    val cardNumber: String,
    val cardExpirationMonth: Int,
    val cardExpirationYear: Int,
    val cardCVC: Int
)