package se.korpi.filebridge.user

import se.korpi.filebridge.plugins.Redis
import se.korpi.filebridge.utils.getEnvProperty
import io.ktor.http.*
import io.ktor.server.application.*

class UserService(private val environment: ApplicationEnvironment) {
    private val db = Redis.pool
    private fun userKey(user: String) = "user:$user" //getEnvProperty(environment, "redis.keySchema.user") + user

    private val userListKey = "users" // getEnvProperty(environment, "redis.keySchema.userList")

    fun createUser(userCredentials: UserCredentials): HttpStatusCode {
        val hashedPassword = Hasher.hashPassword(userCredentials.password)

        val emailInUse = db.sismember(userListKey, userCredentials.email)
        if (emailInUse) {
            return HttpStatusCode.Conflict
        }

        val t = db.multi()
        t.hset(
            userKey(userCredentials.email),
            mapOf(
                "email" to userCredentials.email,
                "password" to hashedPassword
            )
        )
        t.sadd(userListKey, userCredentials.email)  // add email to list of emails in use
        t.exec()

        return HttpStatusCode.Created
    }

    fun loginUser(userCredentials: UserCredentials): HttpStatusCode {
        val hashedPassword: String = db.hget(userKey(userCredentials.email), "password") ?: return HttpStatusCode.NotFound
        val passwordMatch = Hasher.verify(userCredentials.password, hashedPassword)
        if (!passwordMatch)
            return HttpStatusCode.Unauthorized

        return HttpStatusCode.OK
    }

    /** Uses the UserCredentials data class, email must already be in use. */
    fun updatePassword(userCredentials: UserCredentials) {
        val hashedPassword = Hasher.hashPassword(userCredentials.password)

        db.hset(
            userKey(userCredentials.email),
            "password",
            hashedPassword
        )
    }

    fun deleteUser(userEmail: String): HttpStatusCode {
        val userKey = userKey(userEmail)

        if (!db.exists(userKey))
            return HttpStatusCode.NotFound

        db.del(userKey)

        return HttpStatusCode.NoContent
    }
}