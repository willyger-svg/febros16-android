package com.example.commitment

import com.example.core.ProtectionMode
import com.example.database.BulldogDatabase
import com.example.database.CommitmentDao
import com.example.database.CommitmentLockEntity
import com.example.database.UnlockRequestDao
import com.example.database.UnlockRequestEntity
import com.example.security.SecurityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Commitment Lock Subsystem.
 * Enforces self-imposed discipline and prevents impulse deactivation.
 *
 * Implements:
 * - Three Modes: NORMAL, PROTECTED, COMMITMENT_LOCK
 * - Strict Lock Duration (e.g., 24h, 7 days, 30 days)
 * - Waiting-Period / Cooling-Off Unlock (24-hour delay before unlock becomes possible)
 * - Persistent state across app termination and device reboots
 */
class CommitmentEngine(
    private val commitmentDao: CommitmentDao,
    private val unlockRequestDao: UnlockRequestDao
) {

    val commitmentLockFlow: Flow<CommitmentLockEntity?> = commitmentDao.getCommitmentLockFlow()
    val latestUnlockRequestFlow: Flow<UnlockRequestEntity?> = unlockRequestDao.getLatestRequestFlow()

    /**
     * Activates Commitment Lock mode with specified duration, PIN, and waiting period.
     */
    suspend fun activateCommitmentLock(
        durationDays: Int,
        pin: String,
        waitingPeriodHours: Int = 24
    ): Boolean {
        val now = System.currentTimeMillis()
        val durationMillis = durationDays.toLong() * 24L * 60L * 60L * 1000L
        val unlockAvailableAt = now + durationMillis

        val hashResult = SecurityManager.hashPin(pin)

        val lockEntity = CommitmentLockEntity(
            id = 1,
            enabled = true,
            mode = ProtectionMode.COMMITMENT_LOCK.name,
            lockedAt = now,
            unlockAvailableAt = unlockAvailableAt,
            waitingPeriodHours = waitingPeriodHours,
            pinHash = hashResult.hash,
            salt = hashResult.salt,
            status = "ACTIVE"
        )

        commitmentDao.saveCommitmentLock(lockEntity)
        unlockRequestDao.clearRequests()
        return true
    }

    /**
     * Activates PROTECTED mode (PIN required to access settings, but no waiting period).
     */
    suspend fun activateProtectedMode(pin: String): Boolean {
        val now = System.currentTimeMillis()
        val hashResult = SecurityManager.hashPin(pin)

        val lockEntity = CommitmentLockEntity(
            id = 1,
            enabled = true,
            mode = ProtectionMode.PROTECTED.name,
            lockedAt = now,
            unlockAvailableAt = 0L,
            waitingPeriodHours = 0,
            pinHash = hashResult.hash,
            salt = hashResult.salt,
            status = "ACTIVE"
        )

        commitmentDao.saveCommitmentLock(lockEntity)
        unlockRequestDao.clearRequests()
        return true
    }

    /**
     * Switches to NORMAL mode if authorized.
     */
    suspend fun switchToNormalMode(pin: String): Boolean {
        val current = commitmentDao.getCommitmentLock() ?: return true
        if (current.mode == ProtectionMode.NORMAL.name) return true

        val verified = SecurityManager.verifyPin(pin, current.pinHash, current.salt)
        if (!verified) return false

        val lockEntity = current.copy(
            enabled = false,
            mode = ProtectionMode.NORMAL.name,
            status = "INACTIVE"
        )
        commitmentDao.saveCommitmentLock(lockEntity)
        unlockRequestDao.clearRequests()
        return true
    }

    /**
     * Initiates a waiting-period unlock request.
     * Protection remains active during the entire cooling-off period.
     */
    suspend fun requestEarlyUnlock(): UnlockRequestEntity? {
        val current = commitmentDao.getCommitmentLock() ?: return null
        if (current.mode != ProtectionMode.COMMITMENT_LOCK.name) return null

        val now = System.currentTimeMillis()
        val waitingMillis = current.waitingPeriodHours.toLong() * 60L * 60L * 1000L
        val unlockReadyAt = now + waitingMillis

        val request = UnlockRequestEntity(
            requestedAt = now,
            waitingPeriodMillis = waitingMillis,
            unlockReadyAt = unlockReadyAt,
            status = "PENDING"
        )
        val id = unlockRequestDao.insertRequest(request)

        val updatedLock = current.copy(status = "UNLOCK_REQUESTED")
        commitmentDao.saveCommitmentLock(updatedLock)

        return request.copy(id = id)
    }

    /**
     * Finalizes unlock after the waiting period has elapsed and PIN is verified.
     */
    suspend fun completeWaitingPeriodUnlock(pin: String): UnlockResult {
        val current = commitmentDao.getCommitmentLock() ?: return UnlockResult.Error("No active lock")
        val request = unlockRequestDao.getLatestRequest() ?: return UnlockResult.Error("No unlock request found")

        val now = System.currentTimeMillis()
        if (now < request.unlockReadyAt) {
            val remainingMillis = request.unlockReadyAt - now
            return UnlockResult.WaitingPeriodNotElapsed(remainingMillis)
        }

        val pinValid = SecurityManager.verifyPin(pin, current.pinHash, current.salt)
        if (!pinValid) {
            return UnlockResult.InvalidPin
        }

        // Successfully unlocked
        unlockRequestDao.updateRequest(request.copy(status = "COMPLETED"))
        val normalLock = current.copy(
            enabled = false,
            mode = ProtectionMode.NORMAL.name,
            status = "INACTIVE"
        )
        commitmentDao.saveCommitmentLock(normalLock)

        return UnlockResult.Success
    }

    /**
     * Cancels an ongoing unlock request.
     */
    suspend fun cancelUnlockRequest() {
        unlockRequestDao.clearRequests()
        val current = commitmentDao.getCommitmentLock()
        if (current != null && current.status == "UNLOCK_REQUESTED") {
            commitmentDao.saveCommitmentLock(current.copy(status = "ACTIVE"))
        }
    }

    /**
     * Validates if a protected action is currently allowed.
     */
    suspend fun isProtectedActionAllowed(): Boolean {
        val current = commitmentDao.getCommitmentLock() ?: return true
        if (!current.enabled) return true

        return when (current.mode) {
            ProtectionMode.NORMAL.name -> true
            ProtectionMode.PROTECTED.name -> false // Requires PIN verification dialog
            ProtectionMode.COMMITMENT_LOCK.name -> {
                val now = System.currentTimeMillis()
                // If lock target date passed, action allowed
                now >= current.unlockAvailableAt && current.unlockAvailableAt > 0L
            }
            else -> true
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val current = commitmentDao.getCommitmentLock() ?: return false
        return SecurityManager.verifyPin(pin, current.pinHash, current.salt)
    }

    sealed class UnlockResult {
        object Success : UnlockResult()
        object InvalidPin : UnlockResult()
        data class WaitingPeriodNotElapsed(val remainingMillis: Long) : UnlockResult()
        data class Error(val message: String) : UnlockResult()
    }
}
