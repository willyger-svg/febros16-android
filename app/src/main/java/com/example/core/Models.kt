package com.example.core

/**
 * Core models and enumerations for Bulldog Protection System.
 */
enum class ProtectionMode {
    NORMAL,            // Protection active, settings freely configurable
    PROTECTED,         // Sensitive settings guarded by PIN authentication
    COMMITMENT_LOCK    // Strict self-imposed lock: settings locked until target date or waiting period
}

enum class KeywordCategory {
    ADULT_EXPLICIT,
    SEXUAL_ACT,
    EXPLICIT_BODY,
    PORN_SEARCH,
    SLANG,
    MISSPELLINGS,
    SW_KISWAHILI,
    ENGLISH
}

enum class KeywordSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class BlockAction {
    ALLOW,
    WARN,
    BLOCK
}

data class KeywordMatchResult(
    val isBlocked: Boolean,
    val matchedWord: String? = null,
    val normalizedInput: String = "",
    val category: KeywordCategory? = null,
    val severity: KeywordSeverity = KeywordSeverity.LOW,
    val reason: String = ""
)

data class FileScanResult(
    val fileName: String,
    val isBlocked: Boolean,
    val fingerprint: String? = null,
    val reason: String,
    val detectedVia: String // "FILE_NAME" or "FINGERPRINT_HASH"
)
