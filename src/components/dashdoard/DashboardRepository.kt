package com.radumarinescu.components.dashdoard

import com.radumarinescu.components.common.GlobalResponse
import com.radumarinescu.components.users.Address
import com.radumarinescu.components.users.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface DashboardRepository {
    suspend fun fetchAddresses(): GlobalResponse<List<Address>>
    suspend fun deleteAddress(addressId: UUID): GlobalResponse<Boolean>
    suspend fun fetchUserPendingCode(email: String): Long?
    suspend fun fetchUser(email: String): User?
}
