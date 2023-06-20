package eu.filebridge.plugins

import eu.filebridge.file.fileRoutes
import eu.filebridge.user.userRoutes
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        userRoutes(environment)
        fileRoutes(environment)
    }
}
