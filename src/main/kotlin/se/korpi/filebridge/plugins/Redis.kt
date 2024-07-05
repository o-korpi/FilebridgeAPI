package se.korpi.filebridge.plugins

import redis.clients.jedis.JedisPooled

object Redis {
    val pool = JedisPooled("127.0.0.1", 6379)
}
