package com.example.fileprotection

import java.io.InputStream
import java.security.MessageDigest

/**
 * Computes deterministic SHA-256 cryptographic fingerprints for file identity.
 * Used for Rename-Protection (Section 8 of specification).
 */
object FileFingerprint {

    fun hashBytes(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun hashStream(inputStream: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            digest.update(buffer, 0, read)
        }
        val hash = digest.digest()
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun hashString(content: String): String {
        return hashBytes(content.toByteArray(Charsets.UTF_8))
    }
}
