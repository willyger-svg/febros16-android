package com.example.fileprotection

import com.example.core.FileScanResult
import com.example.database.BlockedFileDao
import com.example.database.BlockedFileEntity
import com.example.keywordengine.KeywordEngine

/**
 * Offline File Protection & Identity Engine.
 * Implements:
 * 1. File-name filtering (Section 7)
 * 2. Cryptographic fingerprint / rename protection (Section 8)
 *
 * If a file was previously blocked as explicit_keyword.mp4, and the user renames
 * it to harmless movie.mp4, the stored SHA-256 fingerprint will still flag and block it.
 */
class FileProtectionEngine(
    private val keywordEngine: KeywordEngine,
    private val blockedFileDao: BlockedFileDao? = null
) {
    private val COMMON_MEDIA_EXTENSIONS = setOf(
        "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "m4v", "3gp",
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic",
        "zip", "rar", "tar", "apk", "bin"
    )

    /**
     * Sanitizes and separates file extension from base file name.
     */
    fun cleanFileName(fileName: String): String {
        val lastDot = fileName.lastIndexOf('.')
        return if (lastDot > 0 && lastDot < fileName.length - 1) {
            val ext = fileName.substring(lastDot + 1).lowercase()
            if (COMMON_MEDIA_EXTENSIONS.contains(ext)) {
                fileName.substring(0, lastDot)
            } else {
                fileName
            }
        } else {
            fileName
        }
    }

    /**
     * Scans a file by name and optional byte content / fingerprint.
     */
    suspend fun scanFile(
        fileName: String,
        fileBytes: ByteArray? = null,
        providedFingerprint: String? = null
    ): FileScanResult {
        val fingerprint = providedFingerprint ?: fileBytes?.let { FileFingerprint.hashBytes(it) }

        // Step 1: Check cryptographic fingerprint if available (Rename Protection)
        if (!fingerprint.isNullOrBlank() && blockedFileDao != null) {
            val existing = blockedFileDao.findByFingerprint(fingerprint)
            if (existing != null) {
                return FileScanResult(
                    fileName = fileName,
                    isBlocked = true,
                    fingerprint = fingerprint,
                    reason = "Known blocked content fingerprint matched (Original name: '${existing.originalName}')",
                    detectedVia = "FINGERPRINT_HASH"
                )
            }
        }

        // Step 2: Check file name with Keyword Engine
        val baseName = cleanFileName(fileName)
        val matchResult = keywordEngine.evaluate(baseName)

        if (matchResult.isBlocked) {
            // Register fingerprint in database if we have it, so future renames are tracked!
            if (!fingerprint.isNullOrBlank() && blockedFileDao != null) {
                blockedFileDao.insertBlockedFile(
                    BlockedFileEntity(
                        fingerprint = fingerprint,
                        originalName = fileName,
                        status = "BLOCKED",
                        reason = matchResult.reason
                    )
                )
            }

            return FileScanResult(
                fileName = fileName,
                isBlocked = true,
                fingerprint = fingerprint,
                reason = "Filename contains adult restricted term (${matchResult.reason})",
                detectedVia = "FILE_NAME"
            )
        }

        return FileScanResult(
            fileName = fileName,
            isBlocked = false,
            fingerprint = fingerprint,
            reason = "File passed validation",
            detectedVia = "NONE"
        )
    }
}
