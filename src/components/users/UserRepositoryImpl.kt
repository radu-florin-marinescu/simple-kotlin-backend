package com.radumarinescu.components.users

import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.radumarinescu.Constants
import com.radumarinescu.Constants.COLLECTION_NAME_MESSAGES
import com.radumarinescu.Constants.COLLECTION_NAME_USERS
import com.radumarinescu.Constants.DB_NAME
import com.radumarinescu.components.addresses.Address
import com.radumarinescu.components.common.GlobalResponse
import components.messages.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import java.util.*

class UserRepositoryImpl(client: CoroutineClient) : KoinComponent, UserRepository {

    private val userCollection = client
        .getDatabase(DB_NAME)
        .getCollection<User>(Constants.COLLECTION_NAME_USERS)

    private val addressCollection = client
        .getDatabase(DB_NAME)
        .getCollection<Address>(Constants.COLLECTION_NAME_ADDRESSES)


    override suspend fun checkIfUserExists(email: String): Boolean =
        withContext(Dispatchers.IO) {
            userCollection
                .findOne(User::email eq email) != null
        }

    override suspend fun checkPassword(credentials: LoginRequest): Boolean =
        withContext(Dispatchers.IO) {
            val user = userCollection
                .findOne(User::email eq credentials.email)

            credentials.password == user?.password
        }

    override suspend fun fetchUserWithEmail(email: String): User? =
        withContext(Dispatchers.IO) {
            userCollection
                .findOne(User::email eq email)
        }

    override suspend fun fetchUser(uuid: UUID): User? =
        withContext(Dispatchers.IO) {
            userCollection
                .findOne(User::id eq uuid)
        }


    override suspend fun insertUser(user: User) : GlobalResponse<User> =
        withContext(Dispatchers.IO) {
            userCollection
                .insertOne(user)
                .wasAcknowledged()

            GlobalResponse(
                data = user,
                success = true
            )
        }

    override suspend fun fetchAddressesForUser(userId: UUID): GlobalResponse<List<Address>> =
        withContext(Dispatchers.IO) {
            GlobalResponse(
                data = addressCollection
                    .find(Address::userId eq userId)
                    .toList(),
                success = true
            )
        }

    override suspend fun insertAddressForUser(address: Address): GlobalResponse<Address> =
        withContext(Dispatchers.IO) {
            addressCollection
                .insertOne(address)
                .wasAcknowledged()

            GlobalResponse(
                data = address,
                success = true
            )
        }
}