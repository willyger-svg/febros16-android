package com.example.networkprotection

/**
 * Network and DNS filtering module for Bulldog Shield.
 * Provides:
 * 1. Private DNS (DoT / DoH) family protection recommendations
 * 2. Adult-specific Top-Level Domains (TLDs) and common adult hostnames catalog
 * 3. Technical analysis of Android VPN capabilities and constraints
 */
object NetworkProtectionManager {

    data class DnsProvider(
        val name: String,
        val host: String,
        val description: String,
        val primaryIpv4: String,
        val secondaryIpv4: String
    )

    val RECOMMENDED_FAMILY_DNS = listOf(
        DnsProvider(
            name = "Cloudflare 1.1.1.3 (Malware + Adult Blocker)",
            host = "family.cloudflare-dns.com",
            description = "Blocks adult websites, pornography, and phishing domains at the DNS level globally across all apps.",
            primaryIpv4 = "1.1.1.3",
            secondaryIpv4 = "1.0.0.3"
        ),
        DnsProvider(
            name = "CleanBrowsing Adult Filter",
            host = "adult-filter-dns.cleanbrowsing.org",
            description = "Enforces SafeSearch on Google, Bing, YouTube and blocks adult sites and mixed content domains.",
            primaryIpv4 = "185.228.168.10",
            secondaryIpv4 = "185.228.169.11"
        ),
        DnsProvider(
            name = "AdGuard Family Protection",
            host = "family.adguard-dns.com",
            description = "Blocks adult domains, pornographic advertisements, and trackers.",
            primaryIpv4 = "94.140.14.15",
            secondaryIpv4 = "94.140.15.16"
        )
    )

    val RESTRICTED_TLDS = setOf(
        "xxx", "porn", "adult", "sex", "cam", "tube"
    )

    val KNOWN_ADULT_DOMAINS = listOf(
        "pornhub.com", "xvideos.com", "xnxx.com", "redtube.com", "youporn.com",
        "brazzers.com", "chaturbate.com", "onlyfans.com", "stripchat.com",
        "cam4.com", "spankbang.com", "tube8.com", "beeg.com", "xhamster.com"
    )

    /**
     * Checks if a domain or URL hostname should be blocked under network rules.
     */
    fun evaluateDomain(hostname: String): Boolean {
        val cleanHost = hostname.lowercase().trim()
            .removePrefix("http://")
            .removePrefix("https://")
            .split("/").firstOrNull() ?: return false

        // Check explicit adult TLDs
        val tld = cleanHost.substringAfterLast('.', "")
        if (RESTRICTED_TLDS.contains(tld)) return true

        // Check known adult hosts
        for (blocked in KNOWN_ADULT_DOMAINS) {
            if (cleanHost == blocked || cleanHost.endsWith(".$blocked")) {
                return true
            }
        }

        return false
    }
}
