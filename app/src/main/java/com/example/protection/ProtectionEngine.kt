package com.example.protection

import com.example.commitment.CommitmentEngine
import com.example.core.FileScanResult
import com.example.core.KeywordMatchResult
import com.example.database.BulldogRepository
import com.example.fileprotection.FileProtectionEngine
import com.example.keywordengine.KeywordEngine
import com.example.networkprotection.NetworkProtectionManager

/**
 * Main Protection Orchestrator for Bulldog Shield.
 * Coordinates File Filtering, Search Keyword Filtering, Network Domain Checks,
 * and Commitment Lock enforcement.
 */
class ProtectionEngine(
    val keywordEngine: KeywordEngine,
    val fileEngine: FileProtectionEngine,
    val commitmentEngine: CommitmentEngine,
    val repository: BulldogRepository
) {

    /**
     * Evaluates a search query or input text.
     */
    fun evaluateSearchText(query: String): KeywordMatchResult {
        return keywordEngine.evaluate(query)
    }

    /**
     * Evaluates a file name and optional bytes.
     */
    suspend fun scanFile(
        fileName: String,
        bytes: ByteArray? = null,
        fingerprint: String? = null
    ): FileScanResult {
        return fileEngine.scanFile(fileName, bytes, fingerprint)
    }

    /**
     * Evaluates a URL or domain against network protection rules.
     */
    fun evaluateDomain(domain: String): Boolean {
        return NetworkProtectionManager.evaluateDomain(domain)
    }

    /**
     * Checks if protected settings can be modified based on current Commitment Lock state.
     */
    suspend fun canModifySettings(): Boolean {
        return commitmentEngine.isProtectedActionAllowed()
    }
}
