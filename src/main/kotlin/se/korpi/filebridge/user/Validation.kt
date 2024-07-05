package se.korpi.filebridge.user

import io.ktor.server.plugins.requestvalidation.*

fun RequestValidationConfig.userValidation() {
    validate<UserCredentials> { credentials ->
        if (!credentials.email.contains("@")) ValidationResult.Invalid("Invalid email")
        if (credentials.password.length < 8) ValidationResult.Invalid("Password too short")
        if (credentials.password.length >= 256) ValidationResult.Invalid("Password too long")
        ValidationResult.Valid
    }
}
