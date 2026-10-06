package com.example.keywordengine

import com.example.core.KeywordCategory
import com.example.core.KeywordSeverity
import com.example.database.KeywordEntity

/**
 * Curated offline seed dictionary shipped with Bulldog Shield.
 * Includes categorized adult-explicit words in English, Swahili (Kiswahili),
 * Slang, Common Misspellings, and Search patterns.
 */
object SeedDictionary {

    data class SeedEntry(
        val word: String,
        val category: KeywordCategory,
        val severity: KeywordSeverity = KeywordSeverity.HIGH
    )

    val INITIAL_SEEDS: List<SeedEntry> = listOf(
        // ADULT_EXPLICIT (English Core)
        SeedEntry("porn", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("pornography", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("porno", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("xxx", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("hentai", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("nsfw", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.HIGH),
        SeedEntry("erotic", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.MEDIUM),
        SeedEntry("erotica", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.MEDIUM),
        SeedEntry("onlyfans", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.HIGH),
        SeedEntry("camgirl", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.HIGH),
        SeedEntry("xrated", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("brazzers", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("pornhub", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("xvideos", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("redtube", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("xnxx", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("youporn", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("spankbang", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),
        SeedEntry("rule34", KeywordCategory.ADULT_EXPLICIT, KeywordSeverity.CRITICAL),

        // SEXUAL_ACT
        SeedEntry("blowjob", KeywordCategory.SEXUAL_ACT, KeywordSeverity.CRITICAL),
        SeedEntry("handjob", KeywordCategory.SEXUAL_ACT, KeywordSeverity.CRITICAL),
        SeedEntry("cumshot", KeywordCategory.SEXUAL_ACT, KeywordSeverity.CRITICAL),
        SeedEntry("creampie", KeywordCategory.SEXUAL_ACT, KeywordSeverity.CRITICAL),
        SeedEntry("gangbang", KeywordCategory.SEXUAL_ACT, KeywordSeverity.CRITICAL),
        SeedEntry("deepthroat", KeywordCategory.SEXUAL_ACT, KeywordSeverity.CRITICAL),
        SeedEntry("masturbation", KeywordCategory.SEXUAL_ACT, KeywordSeverity.HIGH),
        SeedEntry("masturbate", KeywordCategory.SEXUAL_ACT, KeywordSeverity.HIGH),
        SeedEntry("orgasm", KeywordCategory.SEXUAL_ACT, KeywordSeverity.MEDIUM),
        SeedEntry("penetration", KeywordCategory.SEXUAL_ACT, KeywordSeverity.HIGH),
        SeedEntry("intercourse", KeywordCategory.SEXUAL_ACT, KeywordSeverity.LOW),
        SeedEntry("ejaculation", KeywordCategory.SEXUAL_ACT, KeywordSeverity.MEDIUM),
        SeedEntry("anal", KeywordCategory.SEXUAL_ACT, KeywordSeverity.HIGH),
        SeedEntry("threesome", KeywordCategory.SEXUAL_ACT, KeywordSeverity.HIGH),
        SeedEntry("bukkake", KeywordCategory.SEXUAL_ACT, KeywordSeverity.CRITICAL),

        // EXPLICIT_BODY
        SeedEntry("dildo", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.HIGH),
        SeedEntry("vibrator", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.MEDIUM),
        SeedEntry("fleshlight", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.CRITICAL),
        SeedEntry("boobs", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.MEDIUM),
        SeedEntry("tits", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.HIGH),
        SeedEntry("pussy", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.HIGH),
        SeedEntry("vagina", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.LOW),
        SeedEntry("penis", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.LOW),
        SeedEntry("cock", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.HIGH),
        SeedEntry("dick", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.HIGH),
        SeedEntry("nude", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.HIGH),
        SeedEntry("naked", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.MEDIUM),
        SeedEntry("nudity", KeywordCategory.EXPLICIT_BODY, KeywordSeverity.MEDIUM),

        // PORN_SEARCH
        SeedEntry("free porn", KeywordCategory.PORN_SEARCH, KeywordSeverity.CRITICAL),
        SeedEntry("sex video", KeywordCategory.PORN_SEARCH, KeywordSeverity.CRITICAL),
        SeedEntry("adult video", KeywordCategory.PORN_SEARCH, KeywordSeverity.CRITICAL),
        SeedEntry("adult movie", KeywordCategory.PORN_SEARCH, KeywordSeverity.HIGH),
        SeedEntry("naked girls", KeywordCategory.PORN_SEARCH, KeywordSeverity.HIGH),
        SeedEntry("nude photos", KeywordCategory.PORN_SEARCH, KeywordSeverity.HIGH),
        SeedEntry("live sex", KeywordCategory.PORN_SEARCH, KeywordSeverity.CRITICAL),
        SeedEntry("hot webcam", KeywordCategory.PORN_SEARCH, KeywordSeverity.HIGH),
        SeedEntry("milf", KeywordCategory.PORN_SEARCH, KeywordSeverity.HIGH),
        SeedEntry("stepmom", KeywordCategory.PORN_SEARCH, KeywordSeverity.HIGH),
        SeedEntry("stepsister", KeywordCategory.PORN_SEARCH, KeywordSeverity.HIGH),

        // SW_KISWAHILI (Swahili adult terms and slang)
        SeedEntry("ngono", KeywordCategory.SW_KISWAHILI, KeywordSeverity.HIGH),
        SeedEntry("pono", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("utupu", KeywordCategory.SW_KISWAHILI, KeywordSeverity.HIGH),
        SeedEntry("filamu chafu", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("video chafu", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("picha chafu", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("kutombana", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("kutomba", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("kufirwa", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("kufira", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("makalio makubwa", KeywordCategory.SW_KISWAHILI, KeywordSeverity.MEDIUM),
        SeedEntry("kuma", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("mboo", KeywordCategory.SW_KISWAHILI, KeywordSeverity.CRITICAL),
        SeedEntry("sherati", KeywordCategory.SW_KISWAHILI, KeywordSeverity.MEDIUM),
        SeedEntry("usherati", KeywordCategory.SW_KISWAHILI, KeywordSeverity.HIGH),
        SeedEntry("uzinzi", KeywordCategory.SW_KISWAHILI, KeywordSeverity.MEDIUM),
        SeedEntry("mzinzi", KeywordCategory.SW_KISWAHILI, KeywordSeverity.MEDIUM),
        SeedEntry("uchafu", KeywordCategory.SW_KISWAHILI, KeywordSeverity.LOW),
        SeedEntry("usagaji", KeywordCategory.SW_KISWAHILI, KeywordSeverity.HIGH),
        SeedEntry("kusagana", KeywordCategory.SW_KISWAHILI, KeywordSeverity.HIGH),
        SeedEntry("shangazi tamu", KeywordCategory.SW_KISWAHILI, KeywordSeverity.HIGH),
        SeedEntry("wasichana uchi", KeywordCategory.SW_KISWAHILI, KeywordSeverity.HIGH),
        SeedEntry("mwanamke uchi", KeywordCategory.SW_KISWAHILI, KeywordSeverity.HIGH),

        // SLANG & MISSPELLINGS
        SeedEntry("prn", KeywordCategory.MISSPELLINGS, KeywordSeverity.CRITICAL),
        SeedEntry("pron", KeywordCategory.MISSPELLINGS, KeywordSeverity.CRITICAL),
        SeedEntry("p0rn", KeywordCategory.MISSPELLINGS, KeywordSeverity.CRITICAL),
        SeedEntry("p*rn", KeywordCategory.MISSPELLINGS, KeywordSeverity.CRITICAL),
        SeedEntry("sexxx", KeywordCategory.MISSPELLINGS, KeywordSeverity.CRITICAL),
        SeedEntry("sexx", KeywordCategory.MISSPELLINGS, KeywordSeverity.HIGH),
        SeedEntry("sxxy", KeywordCategory.MISSPELLINGS, KeywordSeverity.MEDIUM),
        SeedEntry("horny", KeywordCategory.SLANG, KeywordSeverity.MEDIUM),
        SeedEntry("nsfw gif", KeywordCategory.SLANG, KeywordSeverity.HIGH),
        SeedEntry("striptease", KeywordCategory.SLANG, KeywordSeverity.MEDIUM),
        SeedEntry("leaked nudes", KeywordCategory.SLANG, KeywordSeverity.CRITICAL)
    )

    fun toEntities(): List<KeywordEntity> {
        return INITIAL_SEEDS.map { seed ->
            KeywordEntity(
                word = seed.word,
                normalizedWord = TextNormalizer.normalize(seed.word),
                category = seed.category.name,
                enabled = true,
                severity = seed.severity.name
            )
        }
    }
}
