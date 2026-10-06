package com.example

import com.example.fileprotection.FileFingerprint
import com.example.fileprotection.FileProtectionEngine
import com.example.keywordengine.KeywordEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FileProtectionTest {

    @Test
    fun testFileFingerprint_deterministicSha256() {
        val sample1 = "sample content 1"
        val hash1 = FileFingerprint.hashString(sample1)
        val hash2 = FileFingerprint.hashString(sample1)

        assertEquals("Hash must be deterministic", hash1, hash2)
        assertEquals(64, hash1.length) // SHA-256 produces 64 hex chars
    }

    @Test
    fun testFileProtectionEngine_extensionStrippingAndDetection() = runBlocking {
        val engine = FileProtectionEngine(KeywordEngine())

        // 1. Explicit keyword in filename is blocked
        val result1 = engine.scanFile("explicit_porn_video.mp4")
        assertTrue("Expected explicit file name to be blocked", result1.isBlocked)

        // 2. Clean movie is allowed
        val result2 = engine.scanFile("clean_family_vacation.mp4")
        assertFalse("Expected clean file name to be allowed", result2.isBlocked)

        // 3. Swahili explicit file name is blocked
        val result3 = engine.scanFile("ngono_za_usiku.mkv")
        assertTrue("Expected Swahili explicit file name to be blocked", result3.isBlocked)
    }
}
