package eu.filebridge.plugins

import redis.clients.jedis.JedisPooled

object Redis {
    val pool = JedisPooled(
        "localhost",
        6379
    )
}

