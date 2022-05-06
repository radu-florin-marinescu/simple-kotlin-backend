package com.radumarinescu.components.users

interface UserRepository {
    suspend fun checkIfUserExists(email: String): Boolean
    suspend fun checkPassword(credentials: LoginRequest): Boolean
    suspend fun insert(user: User)
    suspend fun fetch(email: String): User?
}

