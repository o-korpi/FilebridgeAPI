package eu.filebridge.plugins

import eu.filebridge.utils.getEnvProperty
import io.ktor.server.application.*
import redis.clients.jedis.JedisPooled

object Redis {
    private var environment: ApplicationEnvironment? = null
    private fun getEnv(): ApplicationEnvironment = environment!!

    fun setEnv(environment: ApplicationEnvironment) {
        this.environment = environment
    }

    val pool = JedisPooled(
        getEnvProperty(getEnv(), "redis.host"),
        getEnvProperty(getEnv(), "redis.port").toInt()
    )
}


fun Application.configureRedis() {
    Redis.setEnv(environment)
}
