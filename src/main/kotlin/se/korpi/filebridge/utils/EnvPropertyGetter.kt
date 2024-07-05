package se.korpi.filebridge.utils

import io.ktor.server.application.*

fun getEnvProperty(environment: ApplicationEnvironment?, path: String): String =
    environment?.config?.property(path).toString()
