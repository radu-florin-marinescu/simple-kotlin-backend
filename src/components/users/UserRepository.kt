package com.radumarinescu.components.users

import com.radumarinescu.components.addresses.Address
import com.radumarinescu.components.common.GlobalResponse
import java.util.*

interface UserRepository {
    suspend fun checkIfUserExists(email: String): Boolean
    suspend fun fetchUserWithEmail(email: String): User?
    suspend fun checkPassword(credentials: LoginRequest): Boolean

    suspend fun fetchUser(uuid: UUID): User?
    suspend fun insertUser(user: User) : GlobalResponse<User>

    suspend fun fetchAddressesForUser(userId: UUID): GlobalResponse<List<Address>>
    suspend fun insertAddressForUser(address: Address): GlobalResponse<Address>
}

