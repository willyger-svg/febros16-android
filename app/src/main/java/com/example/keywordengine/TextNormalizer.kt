package com.example.keywordengine

import java.text.Normalizer
import java.util.Locale

/**
 * Text normalizer that prepares text for robust keyword matching.
 * Handles:
 * - Lowercasing
 * - Accent/diacritic stripping (NFD)
 * - Leetspeak & character obfuscation substitutions (e.g. @ -> a, 0 -> o, $ -> s, 1 -> i)
 * - Repeated character collapsing (e.g. "poooorrrrn" -> "porn")
 * - Delimiter normalization (spaces, underscores, hyphens, dots, slashes)
 */
object TextNormalizer {

    private val LEETSPEAK_MAP = mapOf(
        '@' to 'a',
        '4' to 'a',
        '8' to 'b',
        '3' to 'e',
        '1' to 'i',
        '!' to 'i',
        '|' to 'i',
        '0' to 'o',
        '$' to 's',
        '5' to 's',
        '7' to 't',
        '+' to 't',
        'v' to 'u',
        'z' to 's'
    )

    /**
     * Standard normalization: lowercasing, accent stripping, leet conversion,
     * delimiter space normalization.
     */
    fun normalize(input: String): String {
        if (input.isBlank()) return ""

        // 1. Lowercase
        var text = input.lowercase(Locale.ROOT)

        // 2. Strip diacritics / accents
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")

        // 3. Leetspeak substitution
        val sb = StringBuilder()
        for (char in text) {
            val mapped = LEETSPEAK_MAP[char] ?: char
            sb.append(mapped)
        }
        text = sb.toString()

        // 4. Normalize separators (dots, hyphens, underscores, slashes, tabs) into spaces
        text = text.replace("[-_./\\\\+~*#,;:|\\[\\](){}<>]+".toRegex(), " ")

        // 5. Trim and collapse multiple spaces into single space
        return text.trim().replace("\\s+".toRegex(), " ")
    }

    /**
     * Compact normalization: removes all spaces and punctuation,
     * collapses repeated characters to max 1-2 consecutive chars.
     * E.g. "p_o_r_n" -> "porn", "p.o.r.n" -> "porn", "p o r n" -> "porn", "poooorrrn" -> "porn"
     */
    fun normalizeCompact(input: String): String {
        val normalized = normalize(input)
        val noSpaces = normalized.replace("\\s+".toRegex(), "")
        return collapseConsecutiveRepeats(noSpaces)
    }

    /**
     * Collapses consecutive identical characters so "poooorrrrn" becomes "porn".
     */
    fun collapseConsecutiveRepeats(input: String, maxRepeats: Int = 1): String {
        if (input.length <= 1) return input
        val sb = StringBuilder()
        var prevChar = '\u0000'
        var count = 0

        for (ch in input) {
            if (ch == prevChar) {
                count++
                if (count <= maxRepeats) {
                    sb.append(ch)
                }
            } else {
                prevChar = ch
                count = 1
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Extracts token words from an input string for discrete token matching.
     */
    fun extractTokens(input: String): List<String> {
        val normalized = normalize(input)
        if (normalized.isBlank()) return emptyList()
        return normalized.split(" ")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }
}
