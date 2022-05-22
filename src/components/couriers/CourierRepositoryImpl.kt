package com.radumarinescu.components.couriers

import com.radumarinescu.Constants
import com.radumarinescu.Constants.DB_NAME
import com.radumarinescu.components.common.GlobalResponse
import components.messages.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import java.util.*

class CourierRepositoryImpl(client: CoroutineClient) : KoinComponent, CourierRepository {

    private val courierCollection = client
        .getDatabase(DB_NAME)
        .getCollection<Courier>(Constants.COLLECTION_NAME_COURIERS)

    override suspend fun fetchCouriers(): GlobalResponse<List<Courier>> =
        withContext(Dispatchers.IO) {
            val couriers = courierCollection
                .find()
                .toList()
            GlobalResponse(
                data = couriers,
                success = true
            )
        }

    override suspend fun fetchCourier(id: UUID): GlobalResponse<Courier?> =
        withContext(Dispatchers.IO) {
            val courier = courierCollection
                .findOne(Courier::id eq id)
            GlobalResponse(
                data = courier,
                success = true
            )
        }

    override suspend fun areCouriersAdded(): Boolean =
        withContext(Dispatchers.IO) {
            courierCollection
                .find()
                .toList().isNotEmpty()
        }

    override suspend fun insertAll(couriers: List<Courier>) {
        withContext(Dispatchers.IO) {
            courierCollection
                .insertMany(couriers)
        }
    }
}