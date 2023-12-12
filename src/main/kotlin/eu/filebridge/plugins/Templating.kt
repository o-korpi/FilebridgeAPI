package eu.filebridge.plugins

import io.ktor.server.application.*
import io.ktor.server.pebble.*

fun Application.configureTemplating() {
    install(Pebble) {

    }
}