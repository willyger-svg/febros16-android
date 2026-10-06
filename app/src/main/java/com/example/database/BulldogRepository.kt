package com.example.database

import com.example.core.ProtectionMode
import com.example.keywordengine.SeedDictionary
import com.example.security.SecurityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Single source of truth repository for Bulldog Shield.
 * Provides abstracted access to Room database for ViewModels and Protection Engines.
 */
class BulldogRepository(private val db: BulldogDatabase) {

    val keywordsFlow: Flow<List<KeywordEntity>> = db.keywordDao().getAllKeywords()
    val blockedFilesFlow: Flow<List<BlockedFileEntity>> = db.blockedFileDao().getAllBlockedFiles()
    val settingsFlow: Flow<ProtectionSettingsEntity?> = db.settingsDao().getSettingsFlow()
    val commitmentLockFlow: Flow<CommitmentLockEntity?> = db.commitmentDao().getCommitmentLockFlow()
    val latestUnlockRequestFlow: Flow<UnlockRequestEntity?> = db.unlockRequestDao().getLatestRequestFlow()
    val protectedAppsFlow: Flow<List<ProtectedAppEntity>> = db.protectedAppDao().getAllProtectedApps()

    suspend fun initializeDefaultsIfNeeded() {
        // 1. Seed keywords if empty
        val count = db.keywordDao().getCount()
        if (count == 0) {
            db.keywordDao().insertAll(SeedDictionary.toEntities())
        }

        // 2. Default settings if missing
        val settings = db.settingsDao().getSettings()
        if (settings == null) {
            db.settingsDao().saveSettings(
                ProtectionSettingsEntity(
                    id = 1,
                    fileProtectionEnabled = true,
                    searchFilteringEnabled = true,
                    keyboardProtectionEnabled = true,
                    networkProtectionEnabled = true,
                    appProtectionEnabled = true,
                    strictMode = true,
                    firstRunCompleted = false
                )
            )
        }

        // 3. Default CommitmentLock state if missing
        val lock = db.commitmentDao().getCommitmentLock()
        if (lock == null) {
            db.commitmentDao().saveCommitmentLock(
                CommitmentLockEntity(
                    id = 1,
                    enabled = false,
                    mode = ProtectionMode.NORMAL.name,
                    lockedAt = 0L,
                    unlockAvailableAt = 0L,
                    waitingPeriodHours = 24,
                    pinHash = "",
                    salt = "",
                    status = "INACTIVE"
                )
            )
        }
    }

    suspend fun getEnabledKeywords(): List<KeywordEntity> {
        return db.keywordDao().getAllEnabledKeywordsList()
    }

    suspend fun addKeyword(word: String, category: String, severity: String) {
        val normalized = com.example.keywordengine.TextNormalizer.normalize(word)
        db.keywordDao().insertKeyword(
            KeywordEntity(
                word = word.trim(),
                normalizedWord = normalized,
                category = category,
                enabled = true,
                severity = severity
            )
        )
    }

    suspend fun deleteKeyword(id: Long) {
        db.keywordDao().deleteKeyword(id)
    }

    suspend fun updateSettings(settings: ProtectionSettingsEntity) {
        db.settingsDao().saveSettings(settings)
    }

    suspend fun saveBlockedFile(file: BlockedFileEntity) {
        db.blockedFileDao().insertBlockedFile(file)
    }

    suspend fun deleteBlockedFile(id: Long) {
        db.blockedFileDao().deleteBlockedFile(id)
    }

    suspend fun addProtectedApp(packageName: String, appName: String, category: String) {
        db.protectedAppDao().insertApp(
            ProtectedAppEntity(packageName = packageName, appName = appName, isBlocked = true, category = category)
        )
    }

    suspend fun removeProtectedApp(packageName: String) {
        db.protectedAppDao().deleteApp(packageName)
    }
}
