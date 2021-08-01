package components.messages

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.messageRoutes() {

    val repository by inject<MessageRepository>()

    route("/message") {

        get {
            val list = repository.fetch()
            call.respond(HttpStatusCode.OK, list)
        }

        put {
            val message = call.receive<Message>()
            repository.insert(
                Message(
                    id = UUID.randomUUID(),
                    text = message.text
                )
            )

            call.respond(HttpStatusCode.OK)
        }

        get("{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respondText(
                    text = "Parameter id was not specified correctly",
                    status = HttpStatusCode.BadRequest
                )

            val uuid = try {
                UUID.fromString(id)
            } catch (e: Exception) {
                return@get call.respondText(
                    text = "Parameter id was not specified correctly,\nAPI exception: ${e.message}",
                    status = HttpStatusCode.BadRequest
                )
            }

            val message = repository.fetch(uuid) ?: return@get call.respondText(
                "No message was found with the requested id (${id})",
                status = HttpStatusCode.NotFound
            )

            call.respond(message)
        }

        delete("{id}") {
            val id = call.parameters["id"]
                ?: return@delete call.respondText(
                    text = "Parameter id was not specified correctly",
                    status = HttpStatusCode.BadRequest
                )

            val uuid = try {
                UUID.fromString(id)
            } catch (e: Exception) {
                return@delete call.respondText(
                    text = "Parameter id was not specified correctly,\nAPI exception: ${e.message}",
                    status = HttpStatusCode.BadRequest
                )
            }

            if (!repository.delete(uuid)) {
                return@delete call.respondText(
                    text = "Message couldn't be deleted",
                    status = HttpStatusCode.BadRequest
                )
            }

            call.respondText(
                text = "Success",
                status = HttpStatusCode.OK
            )
        }

        put("{id}") {
            val id = call.parameters["id"]
                ?: return@put call.respondText(
                    text = "Parameter id was not specified correctly",
                    status = HttpStatusCode.BadRequest
                )

            val message = call.receive<Message>()

            if (message.text.isEmpty()) {
                return@put call.respondText(
                    text = "Parameter text was null or empty",
                    status = HttpStatusCode.BadRequest
                )
            }

            val uuid = try {
                UUID.fromString(id)
            } catch (e: Exception) {
                return@put call.respondText(
                    text = "Parameter id was not specified correctly,\nAPI exception: ${e.message}",
                    status = HttpStatusCode.BadRequest
                )
            }

            val result = repository.update(uuid, message.text) ?: return@put call.respondText(
                text = "No message was found with the requested id (${id})",
                status = HttpStatusCode.NotFound
            )

            call.respond(result)
        }
    }
}