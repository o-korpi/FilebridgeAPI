package eu.filebridge.file

import eu.filebridge.utils.getCallerEmail
import eu.filebridge.utils.getEnvProperty
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.fileRoutes(environment: ApplicationEnvironment?) {
    val service = FileService(environment!!)

    authenticate("auth-jwt") {
        route("/files") {
            post {
                val userEmail = getCallerEmail(call) ?: return@post call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")
                val filesOwned = service.filesOwnedCount(userEmail)
                if (filesOwned > getEnvProperty(environment, "filebridge.maxFiles").toInt()) {
                    return@post call.respond(HttpStatusCode.Forbidden, "Upload limit exceeded.")
                }

                val fileUpload = call.receive<FileUpload>()

                val uuid = service.persistFile(fileUpload)
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or incomplete upload data.")

                call.respond(HttpStatusCode.Created, uuid)
            }

            get {
                val email = getCallerEmail(call)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")
                call.respondText(service.getOwnedFiles(email).toString())
            }

            get("/{fileId}") {
                val owner = getCallerEmail(call)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")

                val requestedFile = call.parameters["fileId"]
                    ?: return@get call.respond(HttpStatusCode.NotFound, "File not found.")

                if (service.getOwnedFiles(owner).map { it.fileId }.contains(requestedFile)) {
                    val file = service.getFileContent(requestedFile)
                    if (file == null)
                        call.respond(HttpStatusCode.NotFound, "File not found. Has it timed out?")
                    else
                        call.respond(file)

                } else
                    call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")
            }

            delete("/{fileId}") {
                val owner = getCallerEmail(call)
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")

                val requestedFile = call.parameters["fileId"] ?:
                    return@delete call.respond(HttpStatusCode.NotFound, "File not found.")

                if (service.getOwnedFiles(owner).map { it.fileId }.contains(requestedFile)) {
                    service.deleteFile(requestedFile, owner)
                    call.respond(HttpStatusCode.NoContent, "Successfully deleted.")
                } else
                    call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")
            }
        }
    }
}
