package com.radumarinescu.components.dashdoard

import com.radumarinescu.Constants
import com.radumarinescu.Constants.DB_NAME
import com.radumarinescu.components.common.GlobalResponse
import com.radumarinescu.components.users.Address
import com.radumarinescu.components.users.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import java.util.*

class DashboardRepositoryImpl(client: CoroutineClient) : KoinComponent, DashboardRepository {

    private val addressCollection = client
        .getDatabase(DB_NAME)
        .getCollection<Address>(Constants.COLLECTION_NAME_ADDRESSES)

    private val userCollection = client
        .getDatabase(DB_NAME)
        .getCollection<User>(Constants.COLLECTION_NAME_USERS)

    override suspend fun fetchAddresses(): GlobalResponse<List<Address>> =
        withContext(Dispatchers.IO) {
            GlobalResponse(
                data = addressCollection
                    .find()
                    .toList(),
                success = true
            )
        }

    override suspend fun deleteAddress(addressId: UUID): GlobalResponse<Boolean> =
        withContext(Dispatchers.IO) {
            addressCollection
                .deleteOne(
                    Address::id eq addressId,
                )
                .wasAcknowledged()
            GlobalResponse(
                success = true
            )
        }

    override suspend fun fetchUserPendingCode(email: String): Long? =
        withContext(Dispatchers.IO) {
            userCollection
                .findOne(User::email eq email)
                ?.pendingConfirmationCode
        }

    override suspend fun fetchUser(email: String): User? =
        withContext(Dispatchers.IO) {
            userCollection
                .findOne(User::email eq email)
        }
}