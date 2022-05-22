package com.radumarinescu.components.users

import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.radumarinescu.Constants
import com.radumarinescu.Constants.DB_NAME
import com.radumarinescu.components.common.GlobalResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import java.util.*

class UserRepositoryImpl(client: CoroutineClient) : KoinComponent, UserRepository {

    private val userCollection = client
        .getDatabase(DB_NAME)
        .getCollection<User>(Constants.COLLECTION_NAME_USERS)

    private val addressCollection = client
        .getDatabase(DB_NAME)
        .getCollection<Address>(Constants.COLLECTION_NAME_ADDRESSES)

    private val cardsCollection = client
        .getDatabase(DB_NAME)
        .getCollection<Card>(Constants.COLLECTION_NAME_CARDS)


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

    override suspend fun updateUserPendingCode(code: Long?, email: String) {
        withContext(Dispatchers.IO) {
            userCollection.findOneAndUpdate(
                User::email eq email,
                set(
                    User::pendingConfirmationCode setTo code,
                )
            )
        }
    }

    override suspend fun updateUserPassword(password: String, email: String) {
        withContext(Dispatchers.IO) {
            userCollection.findOneAndUpdate(
                User::email eq email,
                set(
                    User::password setTo password,
                )
            )
        }
    }

    override suspend fun getUserPendingCode(email: String): Long? =
        withContext(Dispatchers.IO) {
            userCollection
                .findOne(User::email eq email)
                ?.pendingConfirmationCode
        }

    override suspend fun insertUser(user: User): GlobalResponse<User> =
        withContext(Dispatchers.IO) {
            userCollection
                .insertOne(user)
                .wasAcknowledged()

            GlobalResponse(
                data = user,
                success = true
            )
        }

    override suspend fun insertAddressForUser(userId: UUID, address: Address): GlobalResponse<Address?> =
        withContext(Dispatchers.IO) {
            addressCollection
                .insertOne(address.apply {
                    this.userId = userId
                    this.id = UUID.randomUUID()
                })
                .wasAcknowledged()

            GlobalResponse(
                data = address,
                success = true
            )
        }

    override suspend fun fetchAddressesForUser(userId: UUID): GlobalResponse<List<Address>> =
        withContext(Dispatchers.IO) {
            val addresses = addressCollection
                .find(Address::userId eq userId)
                .toList()
            GlobalResponse(
                data = addresses.map { address ->
                    address.userId = null
                    address
                },
                success = true
            )
        }

    override suspend fun fetchAddressForUser(userId: UUID, addressId: UUID): GlobalResponse<Address?> =
        withContext(Dispatchers.IO) {
            val address = addressCollection
                .findOne(
                    Address::userId eq userId,
                    Address::id eq addressId
                )
            GlobalResponse(
                data = address?.apply {
                    this.userId = null
                },
                success = true
            )
        }

    override suspend fun deleteAddressForUser(userId: UUID, addressId: UUID): GlobalResponse<Boolean> =
        withContext(Dispatchers.IO) {
            GlobalResponse(
                data = addressCollection
                    .deleteOne(
                        Address::userId eq userId,
                        Address::id eq addressId
                    )
                    .wasAcknowledged(),
                success = true
            )
        }

    override suspend fun updateAddressForUser(userId: UUID, address: Address): GlobalResponse<Address?> =
        withContext(Dispatchers.IO) {
            GlobalResponse(
                data = addressCollection
                    .findOneAndUpdate(
                        Address::id eq address.id,
                        set(
                            Address::address setTo address.address,
                            Address::placesId setTo address.placesId,
                            Address::latitude setTo address.latitude,
                            Address::longitude setTo address.longitude,
                        ),
                        options = FindOneAndUpdateOptions().apply { returnDocument(ReturnDocument.AFTER) }
                    ),
                success = true
            )
        }

    override suspend fun fetchCardsForUser(userId: UUID): GlobalResponse<List<Card>> =
        withContext(Dispatchers.IO) {
            val cards = cardsCollection
                .find(Card::userId eq userId)
                .toList()
            GlobalResponse(
                data = cards.map { card ->
                    card.userId = null
                    card
                },
                success = true
            )
        }

    override suspend fun insertCardForUser(userId: UUID, card: Card): GlobalResponse<Card?> =
        withContext(Dispatchers.IO) {
            cardsCollection
                .insertOne(card.apply {
                    this.userId = userId
                    this.id = UUID.randomUUID()
                })
                .wasAcknowledged()

            GlobalResponse(
                data = card,
                success = true
            )
        }
}