package eu.filebridge.file

import kotlinx.serialization.Serializable

@Serializable
data class FileUpload(
    val fileName: String,
    val timeToLive: Int,
    val owner: String,
    val data: String
)

@Serializable
data class FileData(
    val fileId: String,
    val fileName: String,
    val timeToLive: Int
)

@Serializable
data class FileContent(
    val fileName: String,
    val data: String
)