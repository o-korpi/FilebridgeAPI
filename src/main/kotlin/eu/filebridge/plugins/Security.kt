package eu.filebridge.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureSecurity() {

    val jwtAudience = environment.config.propertyOrNull("jwt.audience")?.getString() ?: "jwt-audience"
    val jwtDomain = environment.config.propertyOrNull("jwt.domain")?.getString() ?: "https://filebridge.eu/"
    val jwtRealm = environment.config.propertyOrNull("jwt.realm")?.getString() ?: "Filebridge API"
    val jwtSecret = environment.config.propertyOrNull("jwt.secret")?.getString()

    if (jwtRealm == "")
        println("Missing JWT secret")

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience))
                    JWTPrincipal(credential.payload)
                else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
