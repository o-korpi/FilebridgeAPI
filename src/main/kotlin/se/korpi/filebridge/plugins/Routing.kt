package se.korpi.filebridge.plugins

import se.korpi.filebridge.file.fileRoutes
import se.korpi.filebridge.user.userRoutes
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            userRoutes(environment)
            fileRoutes(environment)
        }
    }
}
