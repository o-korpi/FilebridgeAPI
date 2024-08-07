package se.korpi.filebridge.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import java.util.*
import se.korpi.filebridge.user.UserCredentials

/** Create a JWT token for given user credentials. Credentials assumed to be valid. */
fun createToken(userCredentials: UserCredentials, environment: ApplicationEnvironment?): String? {
    val getProperty = { path: String -> environment?.config?.property(path)?.getString() }

    println(getProperty("jwt.audience"))

    return JWT.create()
        .withAudience(getProperty("jwt.audience"))
        .withIssuer(getProperty("jwt.domain"))
        .withClaim("email", userCredentials.email)
        .withExpiresAt(
            Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7 * 31 * 2)) // 60000))
        .sign(Algorithm.HMAC256(getProperty("jwt.secret")))
}
