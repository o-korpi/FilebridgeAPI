package eu.filebridge

import eu.filebridge.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit {
    Redis.pool
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureValidation()
    configureRedis()
    configureRouting()
}
