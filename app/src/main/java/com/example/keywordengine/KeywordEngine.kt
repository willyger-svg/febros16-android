package com.example.keywordengine

import com.example.core.KeywordCategory
import com.example.core.KeywordMatchResult
import com.example.core.KeywordSeverity
import com.example.database.KeywordEntity
import java.util.concurrent.CopyOnWriteArrayList

/**
 * High-performance, offline Keyword Matching Engine.
 * Implements multi-layered inspection:
 * 1. Fast Token Match (boundary-checked words)
 * 2. Phrase Match (multi-word exact sequences)
 * 3. Compact Substring Match (handles "p_o_r_n", "p.o.r.n", "p0rn", etc.)
 * 4. Safe Whitelist Guards (prevents false positives like "import", "button", "document")
 */
class KeywordEngine(
    initialKeywords: List<KeywordEntity> = emptyList()
) {
    private val activeKeywords = CopyOnWriteArrayList<KeywordEntity>()

    init {
        if (initialKeywords.isNotEmpty()) {
            activeKeywords.addAll(initialKeywords)
        } else {
            activeKeywords.addAll(SeedDictionary.toEntities())
        }
    }

    fun updateKeywords(newKeywords: List<KeywordEntity>) {
        activeKeywords.clear()
        activeKeywords.addAll(newKeywords.filter { it.enabled })
    }

    /**
     * Inspects a string against the active offline dictionary.
     * Returns a detailed [KeywordMatchResult].
     */
    fun evaluate(rawInput: String): KeywordMatchResult {
        if (rawInput.isBlank()) {
            return KeywordMatchResult(isBlocked = false)
        }

        val normalized = TextNormalizer.normalize(rawInput)
        val compact = TextNormalizer.normalizeCompact(rawInput)
        val tokens = TextNormalizer.extractTokens(rawInput)

        // 1. Multi-word phrase check (e.g., "filamu chafu", "free sex video")
        for (kw in activeKeywords) {
            val kwNorm = kw.normalizedWord
            if (kwNorm.contains(" ")) {
                if (normalized.contains(kwNorm)) {
                    return buildBlockedResult(kw, normalized, "Matched exact phrase: '${kw.word}'")
                }
            }
        }

        // 2. Token match with word boundaries
        for (kw in activeKeywords) {
            val kwNorm = kw.normalizedWord
            if (!kwNorm.contains(" ")) {
                if (tokens.contains(kwNorm)) {
                    return buildBlockedResult(kw, normalized, "Matched keyword: '${kw.word}'")
                }
            }
        }

        // 3. Compact pattern match (obfuscation: "p_o_r_n", "p-o-r-n", "p0rn")
        // For short keywords (length >= 3), check compact string but guard against common benign substrings
        for (kw in activeKeywords) {
            val kwNorm = kw.normalizedWord
            if (kwNorm.length >= 3 && !isShortBenignPattern(kwNorm)) {
                val compactKw = TextNormalizer.normalizeCompact(kwNorm)
                if (compact.contains(compactKw)) {
                    // Check if false positive in normal english words
                    if (!isFalsePositive(normalized, kwNorm)) {
                        return buildBlockedResult(
                            kw,
                            compact,
                            "Matched obfuscated pattern: '${kw.word}'"
                        )
                    }
                }
            }
        }

        return KeywordMatchResult(
            isBlocked = false,
            normalizedInput = normalized,
            severity = KeywordSeverity.LOW
        )
    }

    private fun buildBlockedResult(
        kw: KeywordEntity,
        input: String,
        reason: String
    ): KeywordMatchResult {
        val cat = try {
            KeywordCategory.valueOf(kw.category)
        } catch (_: Exception) {
            KeywordCategory.ADULT_EXPLICIT
        }
        val sev = try {
            KeywordSeverity.valueOf(kw.severity)
        } catch (_: Exception) {
            KeywordSeverity.HIGH
        }

        return KeywordMatchResult(
            isBlocked = true,
            matchedWord = kw.word,
            normalizedInput = input,
            category = cat,
            severity = sev,
            reason = reason
        )
    }

    /**
     * Prevents false positives like "document", "import", "analyst" (for "anal"),
     * "cockpit" (for "cock"), "assist" (for "ass").
     */
    private fun isFalsePositive(normalizedText: String, keyword: String): Boolean {
        val benignTerms = listOf(
            "document", "import", "analyst", "analysis", "cockpit", "cocktail",
            "button", "assault", "passion", "compass", "classic", "bulletin",
            "cucumber", "assist", "associate", "mass", "bass", "grass", "glass"
        )
        for (benign in benignTerms) {
            if (normalizedText.contains(benign) && benign.contains(keyword)) {
                return true
            }
        }
        return false
    }

    private fun isShortBenignPattern(keyword: String): Boolean {
        return keyword.length <= 2 // e.g. 2-letter tokens should not be compact matched
    }
}
