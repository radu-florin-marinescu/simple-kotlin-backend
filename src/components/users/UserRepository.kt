package com.radumarinescu.components.users

import com.radumarinescu.components.common.GlobalResponse
import java.util.*

interface UserRepository {
    // User management
    suspend fun checkIfUserExists(email: String): Boolean
    suspend fun fetchUserWithEmail(email: String): User?
    suspend fun checkPassword(credentials: LoginRequest): Boolean

    suspend fun fetchUser(uuid: UUID): User?
    suspend fun insertUser(user: User) : GlobalResponse<User>

    // Addresses
    suspend fun insertAddressForUser(userId: UUID, address: Address): GlobalResponse<Address?>
    suspend fun fetchAddressesForUser(userId: UUID): GlobalResponse<List<Address>>
    suspend fun fetchAddressForUser(userId: UUID, addressId: UUID): GlobalResponse<Address?>
    suspend fun deleteAddressForUser(userId: UUID, addressId: UUID): GlobalResponse<Boolean>
    suspend fun updateAddressForUser(userId: UUID, address: Address):GlobalResponse<Address?>
}

