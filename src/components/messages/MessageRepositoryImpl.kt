package components.messages

import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.radumarinescu.Constants.COLLECTION_NAME_MESSAGES
import com.radumarinescu.Constants.DB_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import java.util.*

class MessageRepositoryImpl : KoinComponent, MessageRepository {

    private val client by inject<CoroutineClient>()

    override suspend fun insert(message: Message) {
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Message>(COLLECTION_NAME_MESSAGES)
                .insertOne(message)
        }
    }

    override suspend fun fetch(): List<Message> =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Message>(COLLECTION_NAME_MESSAGES)
                .find()
                .toList()
        }

    override suspend fun fetchPaginated(offset: Int, number: Int) =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Message>(COLLECTION_NAME_MESSAGES)
                .find()
                .skip(offset)
                .limit(number)
                .toList()
        }

    override suspend fun fetchCount() =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Message>(COLLECTION_NAME_MESSAGES)
                .countDocuments()
        }

    override suspend fun fetch(id: UUID): Message? =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Message>(COLLECTION_NAME_MESSAGES)
                .findOne(Message::id eq id)
        }

    override suspend fun delete(id: UUID): Boolean =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Message>(COLLECTION_NAME_MESSAGES)
                .deleteOne(Message::id eq id)
                .wasAcknowledged()
        }

    override suspend fun update(id: UUID, text: String): Message? =
        withContext(Dispatchers.IO) {
            client
                .getDatabase(DB_NAME)
                .getCollection<Message>(COLLECTION_NAME_MESSAGES)
                .findOneAndUpdate(Message::id eq id,
                    setValue(Message::text, text),
                    options = FindOneAndUpdateOptions().apply { returnDocument(ReturnDocument.AFTER) }
                )
        }
}