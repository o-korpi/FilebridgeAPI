package se.korpi.filebridge.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import se.korpi.filebridge.file.fileRoutes
import se.korpi.filebridge.user.userRoutes

fun Application.configureRouting() {
    routing {
        route("/api") {
            userRoutes(environment)
            fileRoutes(environment)
        }
    }
}
