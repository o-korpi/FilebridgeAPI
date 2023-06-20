package eu.filebridge.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import eu.filebridge.user.UserCredentials
import io.ktor.server.application.*
import java.util.*

/** Create a JWT token for given user credentials. Credentials assumed to be valid. */
fun createToken(userCredentials: UserCredentials, environment: ApplicationEnvironment?): String? {
    val getProperty = { path: String -> environment?.config?.property(path)?.getString() }

    return JWT.create()
        .withAudience(getProperty("jwt.audience"))
        .withIssuer(getProperty("jwt.domain"))
        .withClaim("email", userCredentials.email)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
        .sign(Algorithm.HMAC256(getProperty("jwt.secret")))
}