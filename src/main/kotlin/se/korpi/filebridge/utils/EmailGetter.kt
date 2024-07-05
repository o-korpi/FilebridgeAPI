package se.korpi.filebridge.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun getCallerEmail(call: ApplicationCall): String? = call
    .principal<JWTPrincipal>()
    ?.payload
    ?.claims
    ?.get("email")
    ?.toString()
    ?.removeSurrounding("\"")
