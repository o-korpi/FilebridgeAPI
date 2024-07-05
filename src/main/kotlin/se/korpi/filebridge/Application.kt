package se.korpi.filebridge

import se.korpi.filebridge.plugins.configureRouting
import se.korpi.filebridge.plugins.configureSecurity
import se.korpi.filebridge.plugins.configureSerialization
import se.korpi.filebridge.plugins.configureValidation
import se.korpi.filebridge.user.UserCredentials
import se.korpi.filebridge.user.UserService
import se.korpi.filebridge.utils.createToken
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
        if (debugMode)
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
    println("Creating debug user")
    println(service.createUser(debugUser))
    val token = createToken(debugUser, environment)
    println("Token: $token")
}
