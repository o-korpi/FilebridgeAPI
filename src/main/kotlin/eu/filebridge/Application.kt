package eu.filebridge

import eu.filebridge.plugins.*
import eu.filebridge.user.UserCredentials
import eu.filebridge.user.UserService
import eu.filebridge.utils.createToken
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val debugMode = true

    configureSerialization()
    configureSecurity()
    configureValidation()
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
