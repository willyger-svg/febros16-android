package com.example

import com.example.keywordengine.KeywordEngine
import com.example.keywordengine.TextNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class KeywordNormalizationTest {

    private lateinit var keywordEngine: KeywordEngine

    @Before
    fun setup() {
        keywordEngine = KeywordEngine()
    }

    @Test
    fun testTextNormalizer_basicLowercasingAndSpaces() {
        val input = "  Hello   WORLD_test-case  "
        val normalized = TextNormalizer.normalize(input)
        assertEquals("hello world test case", normalized)
    }

    @Test
    fun testTextNormalizer_leetspeakSubstitution() {
        val input = "p0rn"
        val normalized = TextNormalizer.normalize(input)
        assertEquals("porn", normalized)

        val input2 = "@dult"
        val normalized2 = TextNormalizer.normalize(input2)
        assertEquals("adult", normalized2)
    }

    @Test
    fun testTextNormalizer_repeatedCharacterCollapsing() {
        val input = "poooooorrrrrn"
        val compact = TextNormalizer.normalizeCompact(input)
        assertEquals("porn", compact)
    }

    @Test
    fun testKeywordEngine_blocksEnglishExplicit() {
        val result = keywordEngine.evaluate("watch porn video")
        assertTrue("Expected query to be blocked", result.isBlocked)
        assertEquals("porn", result.matchedWord)

        val result2 = keywordEngine.evaluate("free porn download")
        assertTrue("Expected query to be blocked", result2.isBlocked)
        assertEquals("free porn", result2.matchedWord)
    }

    @Test
    fun testKeywordEngine_blocksSwahiliExplicit() {
        val result = keywordEngine.evaluate("tazama filamu chafu leo")
        assertTrue("Expected Swahili phrase to be blocked", result.isBlocked)
        assertEquals("filamu chafu", result.matchedWord)

        val result2 = keywordEngine.evaluate("ngono mtandaoni")
        assertTrue("Expected Swahili word 'ngono' to be blocked", result2.isBlocked)
    }

    @Test
    fun testKeywordEngine_blocksObfuscatedKeyword() {
        val result = keywordEngine.evaluate("p_o_r_n")
        assertTrue("Expected obfuscated keyword to be blocked", result.isBlocked)
    }

    @Test
    fun testKeywordEngine_allowsBenignWordsWithoutFalsePositives() {
        val result1 = keywordEngine.evaluate("important document analysis")
        assertFalse("Benign terms like 'import' and 'analysis' should not be blocked", result1.isBlocked)

        val result2 = keywordEngine.evaluate("classic movie about nature")
        assertFalse("Benign query should not be blocked", result2.isBlocked)
    }
}
