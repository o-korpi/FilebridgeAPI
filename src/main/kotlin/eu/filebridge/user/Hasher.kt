package eu.filebridge.user

import at.favre.lib.crypto.bcrypt.BCrypt

object Hasher {
    fun hashPassword(rawPassword: String): String = BCrypt.withDefaults()
        .hashToString(12, rawPassword.toCharArray())

    fun verify(rawPassword: String, hashedPassword: String): Boolean = BCrypt.verifyer()
        .verify(rawPassword.toCharArray(), hashedPassword.toCharArray())
        .verified
}