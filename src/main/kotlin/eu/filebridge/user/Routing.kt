package eu.filebridge.user

import eu.filebridge.utils.createToken
import eu.filebridge.utils.getCallerEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.pebble.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(environment: ApplicationEnvironment?) {
    val service = UserService(environment!!)

    route("/users") {
        get {
            call.respond(PebbleContent("test.html", mapOf()))
        }


        /** Login. Expects `UserCredentials`. */
        post("/login") {
            runCatching {
                call.receive<UserCredentials>()
            }.onFailure {
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid credentials")
            }.onSuccess { user ->
                when (val status = service.loginUser(user)) {
                    HttpStatusCode.Unauthorized -> call.respond(status, "Unsuccessful login.")
                    HttpStatusCode.NotFound -> call.respond(HttpStatusCode.Unauthorized, "Unsuccessful login. (User does not exist (debug purpose only))")
                    HttpStatusCode.OK -> {
                        val token = createToken(user, environment)
                        call.respond(status, hashMapOf("token" to token))
                    }
                }
            }
        }

        /** Register. Expects `UserCredentials`. */
        post {
            val userCredentials = call.receive<UserCredentials>()
            when (val status = service.createUser(userCredentials)) {
                HttpStatusCode.Conflict -> call.respond(status, "Email already in use.")
                HttpStatusCode.Created -> call.respond(status, "User successfully created.")
            }
        }

        authenticate("auth-jwt") {
            /** Change password. Expects `UserCredentials` */
            patch("/{userId}/password") {
                val newCredentials = call.receive<UserCredentials>()
                val callerEmail = getCallerEmail(call) ?:
                    return@patch call.respond(HttpStatusCode.NotFound, "Unauthorized access.")

                if (callerEmail != newCredentials.email) {
                    return@patch call.respond(HttpStatusCode.Unauthorized, "User not found.")
                }

                service.updatePassword(newCredentials)

                call.respond(HttpStatusCode.NoContent, "User password successfully changed.")
            }

            /** Delete user. */
            delete {
                val email = getCallerEmail(call) ?:
                    return@delete call.respond(HttpStatusCode.Unauthorized, "Unauthorized access.")

                when (val status = service.deleteUser(email)) {
                    HttpStatusCode.NoContent -> call.respond(status, "User successfully deleted.")
                    HttpStatusCode.NotFound -> call.respond(status, "User not found.")
                    else -> call.respond(HttpStatusCode.InternalServerError, "Something went wrong in user deletion.")
                }
            }
        }
    }
}