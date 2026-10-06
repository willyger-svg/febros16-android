package com.example

import com.example.security.SecurityManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommitmentLockTest {

    @Test
    fun testSecurityManager_hashAndVerifyPin() {
        val pin = "8492"
        val hashResult = SecurityManager.hashPin(pin)

        assertNotNull(hashResult.hash)
        assertNotNull(hashResult.salt)
        assertTrue(hashResult.hash.isNotEmpty())

        // Correct PIN verifies
        val valid = SecurityManager.verifyPin(pin, hashResult.hash, hashResult.salt)
        assertTrue("Expected correct PIN to verify", valid)

        // Incorrect PIN fails
        val invalid = SecurityManager.verifyPin("1234", hashResult.hash, hashResult.salt)
        assertFalse("Expected wrong PIN to fail", invalid)
    }

    @Test
    fun testClockIntegrity_detectsWallClockRollback() {
        val lastWallTime = 1000000L
        val lastMonotonicTime = 50000L
        val currentWallTime = 2000000L
        val currentMonotonicTime = 1050000L

        // Normal progression
        val statusNormal = SecurityManager.checkClockIntegrity(
            lastRecordedWallTime = lastWallTime,
            lastRecordedMonotonicTime = lastMonotonicTime,
            currentWallTime = currentWallTime,
            currentMonotonicTime = currentMonotonicTime
        )
        assertTrue(statusNormal == SecurityManager.ClockIntegrityStatus.OK)

        // Simulated rollback: if recorded wall time is in the future compared to current wall time
        val rolledBackCurrentWallTime = 500000L
        val statusRolledBack = SecurityManager.checkClockIntegrity(
            lastRecordedWallTime = lastWallTime,
            lastRecordedMonotonicTime = lastMonotonicTime,
            currentWallTime = rolledBackCurrentWallTime,
            currentMonotonicTime = currentMonotonicTime
        )
        assertTrue(statusRolledBack == SecurityManager.ClockIntegrityStatus.TAMPERED_WALL_CLOCK_ROLLED_BACK)
    }
}
