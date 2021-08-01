package components.messages

import java.util.*

interface MessageRepository {
    suspend fun insert(message: Message)
    suspend fun fetch(): List<Message>
    suspend fun fetch(id: UUID): Message?
    suspend fun delete(id: UUID): Boolean
    suspend fun update(id: UUID, text: String): Message?
}

