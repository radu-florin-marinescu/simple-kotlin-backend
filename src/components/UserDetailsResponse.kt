package com.radumarinescu.components

import com.radumarinescu.components.addresses.Address
import com.radumarinescu.components.users.UserResponse

data class UserDetailsResponse(
    val user: UserResponse,
    val addresses: List<Address>,
    val orderCount: Int
)