package eu.filebridge.file

import eu.filebridge.plugins.Redis
import eu.filebridge.utils.getEnvProperty
import io.ktor.server.application.*
import java.util.*



class FileService(private val environment: ApplicationEnvironment) {
    private val db = Redis.pool
    private fun fileKey(fileId: String) = "file:$fileId" //getEnvProperty(environment, "redis.keySchema.file") + fileId
    private fun ownerKey(email: String) = "owner:$email" // getEnvProperty(environment, "redis.keySchema.owner") + email

    fun filesOwnedCount(owner: String): Int = db.smembers(ownerKey(owner)).size

    fun persistFile(file: FileUpload, owner: String): UUID? {
        val fileId = UUID.randomUUID()
        runCatching {
            val fileKey = fileKey(fileId.toString())
            val ownerKey = ownerKey(owner)

            val transaction = db.multi()
            transaction.sadd(ownerKey, fileKey)
            transaction.hset(
                fileKey,
                mapOf(
                    "name" to file.fileName,
                    "data" to file.data
                )
            )
            transaction.exec()
            db.expire(fileKey, file.timeToLive.toLong())
        }.onFailure {
            return null
        }

        return fileId
    }

    fun getFileContent(fileId: String): FileContent? {
        val key = fileKey(fileId)
        val data = db.hget(key, "data")
        val name = db.hget(key, "name")

        if (name == null || data == null)
            return null
        if (name == "nil" || data == "nil")
            return null

        return FileContent(
            name,
            data
        )
    }

    fun getOwnedFiles(owner: String): List<FileData> {
        val ownerKey = ownerKey(owner)
        val fileKeys: Set<String> = db.smembers(ownerKey)

        return fileKeys.mapNotNull { key ->
            val fileName = db.hget(key, "name")
            if (fileName == null || fileName == "nil") {
                db.srem(ownerKey, key)
                return@mapNotNull null
            }

            val remainingTime = db.ttl(key)
            val fileId = key.split(":").last()
            FileData(
                fileId,
                fileName,
                remainingTime.toInt()
            )
        }.filter { it.timeToLive > 0 }  // fix weird bug where files remain after expiring
    }

    fun deleteFile(fileId: String, owner: String) {
        val fileKey = fileKey(fileId)
        val ownerKey = ownerKey(owner)
        val transaction = db.multi()
        transaction.srem(ownerKey, fileKey)
        transaction.del(fileKey)
        transaction.exec()
    }
}