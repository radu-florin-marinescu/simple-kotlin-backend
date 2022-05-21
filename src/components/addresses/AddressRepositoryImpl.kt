package com.radumarinescu.components.addresses

import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.radumarinescu.Constants.COLLECTION_NAME_ADDRESSES
import com.radumarinescu.Constants.COLLECTION_NAME_MESSAGES
import com.radumarinescu.Constants.DB_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineClient
import java.util.*

class AddressRepositoryImpl : KoinComponent, AddressRepository {

    private val client by inject<CoroutineClient>()

    override suspend fun insert(address: Address) {
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Address>(COLLECTION_NAME_ADDRESSES)
                .insertOne(address)
        }
    }

    override suspend fun fetch(): List<Address> =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Address>(COLLECTION_NAME_ADDRESSES)
                .find()
                .toList()
        }

    override suspend fun fetch(id: UUID): Address? =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Address>(COLLECTION_NAME_ADDRESSES)
                .findOne(Address::id eq id)
        }

    override suspend fun delete(id: UUID): Boolean =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Address>(COLLECTION_NAME_ADDRESSES)
                .deleteOne(Address::id eq id)
                .wasAcknowledged()
        }

    override suspend fun update(id: UUID, address: Address): Address? =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Address>(COLLECTION_NAME_ADDRESSES)
                .findOneAndUpdate(Address::id eq id,
                    set(
                        Address::address setTo address.address,
                        Address::placesId setTo address.placesId,
                        Address::latitude setTo address.latitude,
                        Address::longitude setTo address.longitude,
                    ),
                    options = FindOneAndUpdateOptions().apply { returnDocument(ReturnDocument.AFTER) }
                )
        }
}