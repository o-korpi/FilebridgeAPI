package eu.filebridge.file

import eu.filebridge.utils.getCallerEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.fileRoutes(environment: ApplicationEnvironment?) {
    val service = FileService(environment!!)

    route("/download") {
        get {
            val filename = call.request.queryParameters["name"]
            val data = call.request.queryParameters["data"]

        }
    }

    authenticate("auth-jwt") {
        route("/files") {
            /** Upload a file. */
            post {
                val userEmail = getCallerEmail(call) ?: return@post call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")
                val filesOwned = service.filesOwnedCount(userEmail)
                if (filesOwned > 10) {
                    return@post call.respond(HttpStatusCode.Forbidden, "Upload limit exceeded.")
                }

                val fileUpload = call.receive<FileUpload>()

                val uuid = service.persistFile(fileUpload, userEmail)
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or incomplete upload data.")

                call.respond(HttpStatusCode.Created, uuid.toString())
            }

            /** Get information about all owned files. */
            get {
                val email = getCallerEmail(call)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")
                call.respond(FileDataList(service.getOwnedFiles(email)))
            }

            /** Get the content of a specific file. Requires ownership of file. */
            get("/{fileId}") {
                val owner = getCallerEmail(call)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")

                val requestedFile = call.parameters["fileId"]
                    ?: return@get call.respond(HttpStatusCode.NotFound, "File not found.")

                println(requestedFile)

                if (service.getOwnedFiles(owner).map { it.fileId }.contains(requestedFile)) {
                    val file = service.getFileContent(requestedFile)
                    if (file == null)
                        call.respond(HttpStatusCode.NotFound, "File not found. Has it timed out?")
                    else
                        call.respond(file)

                } else
                    call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")
            }

            /** Delete a file. Requires ownership of file. */
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
