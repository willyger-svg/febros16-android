package com.example.security

import android.content.Context
import android.os.SystemClock
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Security & Cryptographic Subsystem for Bulldog Shield.
 * - PBKDF2WithHmacSHA256 password/PIN hashing with cryptographic salts
 * - Zero plaintext storage of user credentials
 * - Monotonic clock anti-tampering heuristics (detecting device time manipulation)
 */
object SecurityManager {

    private const val ITERATIONS = 12000
    private const val KEY_LENGTH = 256
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"

    data class PinHashResult(
        val hash: String,
        val salt: String
    )

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPin(pin: String, saltHex: String = generateSalt()): PinHashResult {
        val saltBytes = saltHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val spec: KeySpec = PBEKeySpec(pin.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hashBytes = factory.generateSecret(spec).encoded
        val hashHex = hashBytes.joinToString("") { "%02x".format(it) }
        return PinHashResult(hash = hashHex, salt = saltHex)
    }

    fun verifyPin(enteredPin: String, expectedHash: String, saltHex: String): Boolean {
        if (expectedHash.isBlank() || saltHex.isBlank()) return false
        val computed = hashPin(enteredPin, saltHex)
        return slowEquals(computed.hash, expectedHash)
    }

    /**
     * Constant-time comparison to prevent timing attacks.
     */
    private fun slowEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }

    /**
     * Tamper Detection Check:
     * Validates whether wall-clock time was rolled backwards.
     * Uses [SystemClock.elapsedRealtime()] (monotonic clock that cannot be changed by user clock settings).
     */
    fun checkClockIntegrity(
        lastRecordedWallTime: Long,
        lastRecordedMonotonicTime: Long,
        currentWallTime: Long = System.currentTimeMillis(),
        currentMonotonicTime: Long = try { SystemClock.elapsedRealtime() } catch (_: Exception) { 0L }
    ): ClockIntegrityStatus {
        // If current wall time is significantly behind recorded wall time, clock was rolled back
        if (currentWallTime < lastRecordedWallTime - 60_000L) { // 1 minute grace for sync adjustments
            return ClockIntegrityStatus.TAMPERED_WALL_CLOCK_ROLLED_BACK
        }

        // Check elapsed difference
        val monotonicDelta = currentMonotonicTime - lastRecordedMonotonicTime
        val wallDelta = currentWallTime - lastRecordedWallTime

        if (lastRecordedMonotonicTime > 0 && monotonicDelta > 0) {
            // If wall time moved forward much slower than monotonic time while device was running
            if (wallDelta < (monotonicDelta - 120_000L)) {
                return ClockIntegrityStatus.SUSPICIOUS_CLOCK_DRIFT
            }
        }

        return ClockIntegrityStatus.OK
    }

    enum class ClockIntegrityStatus {
        OK,
        TAMPERED_WALL_CLOCK_ROLLED_BACK,
        SUSPICIOUS_CLOCK_DRIFT
    }
}
