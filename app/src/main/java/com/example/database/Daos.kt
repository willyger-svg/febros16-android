package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDao {
    @Query("SELECT * FROM keywords WHERE enabled = 1")
    fun getAllEnabledKeywords(): Flow<List<KeywordEntity>>

    @Query("SELECT * FROM keywords WHERE enabled = 1")
    suspend fun getAllEnabledKeywordsList(): List<KeywordEntity>

    @Query("SELECT * FROM keywords ORDER BY id DESC")
    fun getAllKeywords(): Flow<List<KeywordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: KeywordEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(keywords: List<KeywordEntity>)

    @Query("DELETE FROM keywords WHERE id = :id")
    suspend fun deleteKeyword(id: Long)

    @Query("SELECT COUNT(*) FROM keywords")
    suspend fun getCount(): Int
}

@Dao
interface BlockedFileDao {
    @Query("SELECT * FROM blocked_files ORDER BY detectedAt DESC")
    fun getAllBlockedFiles(): Flow<List<BlockedFileEntity>>

    @Query("SELECT * FROM blocked_files WHERE fingerprint = :hash LIMIT 1")
    suspend fun findByFingerprint(hash: String): BlockedFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedFile(file: BlockedFileEntity): Long

    @Query("DELETE FROM blocked_files WHERE id = :id")
    suspend fun deleteBlockedFile(id: Long)

    @Query("SELECT COUNT(*) FROM blocked_files")
    fun getBlockedFilesCount(): Flow<Int>
}

@Dao
interface CommitmentDao {
    @Query("SELECT * FROM commitment_lock WHERE id = 1 LIMIT 1")
    fun getCommitmentLockFlow(): Flow<CommitmentLockEntity?>

    @Query("SELECT * FROM commitment_lock WHERE id = 1 LIMIT 1")
    suspend fun getCommitmentLock(): CommitmentLockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCommitmentLock(lock: CommitmentLockEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM protection_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<ProtectionSettingsEntity?>

    @Query("SELECT * FROM protection_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): ProtectionSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: ProtectionSettingsEntity)
}

@Dao
interface ProtectedAppDao {
    @Query("SELECT * FROM protected_apps ORDER BY appName ASC")
    fun getAllProtectedApps(): Flow<List<ProtectedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: ProtectedAppEntity)

    @Query("DELETE FROM protected_apps WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String)
}

@Dao
interface UnlockRequestDao {
    @Query("SELECT * FROM unlock_requests ORDER BY requestedAt DESC LIMIT 1")
    fun getLatestRequestFlow(): Flow<UnlockRequestEntity?>

    @Query("SELECT * FROM unlock_requests ORDER BY requestedAt DESC LIMIT 1")
    suspend fun getLatestRequest(): UnlockRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: UnlockRequestEntity): Long

    @Update
    suspend fun updateRequest(request: UnlockRequestEntity)

    @Query("DELETE FROM unlock_requests")
    suspend fun clearRequests()
}
