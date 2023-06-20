package eu.filebridge.plugins

import eu.filebridge.file.fileRoutes
import eu.filebridge.user.userRoutes
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    /*install(StatusPages) {

        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

     */

    routing {
        userRoutes(environment)
        fileRoutes(environment)
    }
}
