package se.korpi.filebridge.file

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun RequestValidationConfig.fileValidation() {
    validate<FileUpload> { uploadRequest ->
        val legalTtl = setOf(
            60 * 10,  // 10 minutes
            60 * 60,  // 60 minutes
            60 * 60 * 24  // 1 day
        )
        if (!legalTtl.contains(uploadRequest.timeToLive))
            ValidationResult.Invalid("Invalid time to live. Use one of the following (s): ${legalTtl.joinToString(", ")}")

        if (uploadRequest.fileName == "nil" || !uploadRequest.fileName.contains("."))
            ValidationResult.Invalid("Invalid file name")

        ValidationResult.Valid
    }
}