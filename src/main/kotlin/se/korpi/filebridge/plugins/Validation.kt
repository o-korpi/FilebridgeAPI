package se.korpi.filebridge.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import se.korpi.filebridge.file.fileValidation
import se.korpi.filebridge.user.userValidation

fun Application.configureValidation() {
    install(RequestValidation) {
        userValidation()
        fileValidation()
    }
}
