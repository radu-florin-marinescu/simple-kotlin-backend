package com.radumarinescu.components.users

import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.radumarinescu.Constants.COLLECTION_NAME_MESSAGES
import com.radumarinescu.Constants.COLLECTION_NAME_USERS
import com.radumarinescu.Constants.DB_NAME
import components.messages.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import java.util.*

class UserRepositoryImpl : KoinComponent, UserRepository {

    private val client by inject<CoroutineClient>()

    override suspend fun checkIfUserExists(email: String): Boolean =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<User>(COLLECTION_NAME_USERS)
                .findOne(User::email eq email) != null
        }

    override suspend fun checkPassword(credentials: LoginRequest): Boolean =
        withContext(Dispatchers.IO) {
            val user = client
                .getDatabase(DB_NAME)
                .getCollection<User>(COLLECTION_NAME_USERS)
                .findOne(User::email eq credentials.email)

            credentials.password == user?.password
        }

    override suspend fun fetch(email: String): User? =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<User>(COLLECTION_NAME_USERS)
                .findOne(User::email eq email)
        }

    override suspend fun insert(user: User) {
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<User>(COLLECTION_NAME_USERS)
                .insertOne(user)
        }
    }
}