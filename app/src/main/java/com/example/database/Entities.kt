package com.example.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "keywords",
    indices = [Index(value = ["normalizedWord"], unique = true)]
)
data class KeywordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val normalizedWord: String,
    val category: String,
    val enabled: Boolean = true,
    val severity: String = "HIGH"
)

@Entity(
    tableName = "blocked_files",
    indices = [Index(value = ["fingerprint"], unique = true)]
)
data class BlockedFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fingerprint: String, // Cryptographic SHA-256 hash
    val originalName: String,
    val status: String = "BLOCKED",
    val detectedAt: Long = System.currentTimeMillis(),
    val reason: String = "Adult explicit keyword match"
)

@Entity(tableName = "commitment_lock")
data class CommitmentLockEntity(
    @PrimaryKey val id: Long = 1,
    val enabled: Boolean = false,
    val mode: String = "NORMAL", // NORMAL, PROTECTED, COMMITMENT_LOCK
    val lockedAt: Long = 0L,
    val unlockAvailableAt: Long = 0L,
    val waitingPeriodHours: Int = 24, // 24-hour cooling-off period before unlocking
    val pinHash: String = "",
    val salt: String = "",
    val status: String = "INACTIVE" // INACTIVE, ACTIVE, UNLOCK_REQUESTED, EXPIRED
)

@Entity(tableName = "protected_apps")
data class ProtectedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val category: String = "BROWSER"
)

@Entity(tableName = "protection_settings")
data class ProtectionSettingsEntity(
    @PrimaryKey val id: Long = 1,
    val fileProtectionEnabled: Boolean = true,
    val searchFilteringEnabled: Boolean = true,
    val keyboardProtectionEnabled: Boolean = true,
    val networkProtectionEnabled: Boolean = true,
    val appProtectionEnabled: Boolean = true,
    val strictMode: Boolean = true,
    val firstRunCompleted: Boolean = false
)

@Entity(tableName = "unlock_requests")
data class UnlockRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestedAt: Long = System.currentTimeMillis(),
    val waitingPeriodMillis: Long = 24 * 60 * 60 * 1000L,
    val unlockReadyAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
    val status: String = "PENDING" // PENDING, READY, COMPLETED, CANCELLED
)
