package com.radumarinescu.components.addresses

import java.util.*

interface AddressRepository {
    suspend fun insert(address: Address)
    suspend fun fetch(): List<Address>
    suspend fun fetch(id: UUID): Address?
    suspend fun delete(id: UUID): Boolean
    suspend fun update(id: UUID, address: Address): Address?
}

