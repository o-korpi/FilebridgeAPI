package eu.filebridge.plugins

import eu.filebridge.file.fileValidation
import eu.filebridge.user.userValidation
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidation() {
    install(RequestValidation) {
        userValidation()
        fileValidation()
    }
}
