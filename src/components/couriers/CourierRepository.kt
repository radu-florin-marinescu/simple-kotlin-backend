package com.radumarinescu.components.couriers

import com.radumarinescu.components.common.GlobalResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface CourierRepository {

    suspend fun fetchCouriers(): GlobalResponse<List<Courier>>
    suspend fun fetchCourier(id: UUID): GlobalResponse<Courier?>
    suspend fun areCouriersAdded(): Boolean
    suspend fun insertAll(couriers: List<Courier>)
}