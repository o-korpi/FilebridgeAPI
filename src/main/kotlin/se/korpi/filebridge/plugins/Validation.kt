package se.korpi.filebridge.plugins

import se.korpi.filebridge.file.fileValidation
import se.korpi.filebridge.user.userValidation
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidation() {
    install(RequestValidation) {
        userValidation()
        fileValidation()
    }
}
