package se.korpi.filebridge.user

import kotlinx.serialization.Serializable

/** Simple data class containing user email and unhashed password */
@Serializable
data class UserCredentials(
    val email: String,
    val password: String
)
