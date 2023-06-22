package eu.filebridge

import eu.filebridge.plugins.configureRouting
import eu.filebridge.plugins.configureSecurity
import eu.filebridge.plugins.configureSerialization
import eu.filebridge.plugins.configureValidation
import eu.filebridge.user.UserCredentials
import eu.filebridge.user.UserService
import eu.filebridge.utils.createToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val debugMode = true

    configureSerialization()
    configureSecurity()
    configureValidation()
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()  // @TODO: Don't do this in production if possible. Try to limit it.
    }
    if (debugMode) {
        configureDebugMode()
    }
    configureRouting()
}

fun Application.configureDebugMode() {
    val service = UserService(environment)
    val debugUser = UserCredentials(
        "debug@email.com",
        "password123"
    )

    service.createUser(debugUser)
    val token = createToken(debugUser, environment)
    println("Token: $token")
}
