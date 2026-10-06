package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Honest Technical Architecture & Android OS Limitations Explorer.
 * Strictly fulfills Section 28 of the Engineering Specification:
 * WHAT WE WANT -> ANDROID API -> WHAT IT CAN ACTUALLY DO -> LIMITATION -> OUR FALLBACK
 */
@Composable
fun AndroidLimitationsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("android_limitations_lazy_column")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Technical Honesty & OS Boundaries",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF8FAFC)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bulldog Shield haitoi ahadi hewa. Android ina mipaka ya kiusalama iliyowekwa na mfumo wa uendeshaji. Hapa tunaeleza ukweli halisi wa kiufundi kuhusu kile mfumo unachoweza na usichoweza kufanya.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Section 1: Uninstall Prevention
        item {
            LimitationCard(
                featureTitle = "1. App Uninstallation Protection",
                whatWeWant = "Kuzuia mtumiaji asifute (uninstall) app akiwa kwenye Commitment Lock.",
                androidApi = "DeviceAdminReceiver & DeviceOwner / DevicePolicyManager",
                whatItCanDo = "Inaweza kuzuia uninstallation ya kawaida ya haraka (direct drag to uninstall). Mtumiaji akijaribu kuondoa admin, Android inatuma onyo na kutoa uwezekano wa kuzuia.",
                limitation = "App ya kawaida isiyo na 'Device Owner provisioning' haiwezi kujifanya kuwa haiwezi kufutika milele. Mtumiaji mwenye nia anaweza kutoa admin privileges kwenye Settings za simu.",
                fallback = "Tunatumia DeviceAdminReceiver kutoa friction, na Commitment Lock inarekodi hali ya kuanza upya kwenye SQLite/Room. Mfumo unahifadhi PIN kwenye PBKDF2."
            )
        }

        // Section 2: Safe Mode
        item {
            LimitationCard(
                featureTitle = "2. Safe Mode Restart",
                whatWeWant = "Kuzuia mtumiaji asiwashe simu kwenye Safe Mode ili kukwepa ulinzi.",
                androidApi = "Android Boot & Kernel init sequence",
                whatItCanDo = "Kutambua wakati simu inapowashwa kawaida baada ya Safe Mode kupitia BootReceiver.",
                limitation = "Safe Mode ni kipengele cha Android Kernel/ROM kwa ajili ya matengenezo. Hakuna third-party app inayoweza kuzuia simu kuingia Safe Mode bila root au custom OEM ROM.",
                fallback = "Tunapotoka Safe Mode, BootReceiver inasoma tena Room database mara moja na kuwasha tena Commitment Lock na ulinzi bila kupoteza mipangilio."
            )
        }

        // Section 3: Factory Reset
        item {
            LimitationCard(
                featureTitle = "3. Factory Reset Boundary",
                whatWeWant = "Kuzuia simu isifanyiwe Factory Reset.",
                androidApi = "Recovery Mode & System Wipe Partition",
                whatItCanDo = "DevicePolicyManager inaweza kuzuia Factory Reset tu kama app imepewa 'Device Owner' hadhi wakati simu inapofunguliwa kwa mara ya kwanza kabisa (MDM/Enterprise provisioning).",
                limitation = "Kama app ni ya kawaida, Factory Reset itafuta kila kitu pamoja na app yenyewe.",
                fallback = "Tunatambua kuwa Factory Reset ni boundary ya kiwango cha OS, hivyo ulinzi wetu umeundwa kusaidia mapambano ya kila siku (urge control) badala ya kuharibu mfumo mzima wa simu."
            )
        }

        // Section 4: 3rd-Party App Text Inspection
        item {
            LimitationCard(
                featureTitle = "4. Third-Party Search Inspection",
                whatWeWant = "Kukagua kila kitu kinachoandikwa ndani ya apps zote kama Chrome, Twitter, Telegram.",
                androidApi = "AccessibilityService & InputMethodService (Custom Keyboard)",
                whatItCanDo = "InputMethodService (kibodi yetu) inakagua kila neno linapoandikwa popote kwenye simu kwa njia halisi na salama 100% offline.",
                limitation = "AccessibilityService inaweza kuwa nzito na kufungwa na mtengenezaji wa simu (battery optimization). Pia mtumiaji akibadilisha kibodi, ulinzi wa uandishi unakoma kwenye kibodi nyingine.",
                fallback = "Kutoa kibodi maalum ya Bulldog (InputMethodService) iliyo huru na nyepesi, pamoja na ulinzi wa DNS kuzuia tovuti hata kama kibodi nyingine inatumika."
            )
        }

        // Section 5: Clock Rollbacks / Tampering
        item {
            LimitationCard(
                featureTitle = "5. Device Clock Tampering",
                whatWeWant = "Kuzuia mtumiaji asirudishe saa ya simu nyuma ili kukwepa Commitment Lock.",
                androidApi = "SystemClock.elapsedRealtime() & Persistent Timestamps",
                whatItCanDo = "elapsedRealtime() inapima muda wa processor tangu simu ilipowashwa. Haitegemei saa ya ukutani (wall-clock) na haiwezi kubadilishwa na mtumiaji kwenye Settings.",
                limitation = "Simu ikizimwa na kuwashwa upya, elapsedRealtime inaanza upya kutoka sifuri.",
                fallback = "Tunalinganisha wall-clock na monotonic boot delta kwenye SecurityManager. Mtumiaji akirudisha saa nyuma, mfumo unatambua mara moja (Clock Drift / Tamper Detected)."
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LimitationCard(
    featureTitle: String,
    whatWeWant: String,
    androidApi: String,
    whatItCanDo: String,
    limitation: String,
    fallback: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = featureTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LimitationRow(label = "WHAT WE WANT:", content = whatWeWant, color = Color(0xFFF8FAFC))
            LimitationRow(label = "ANDROID API:", content = androidApi, color = Color(0xFF93C5FD))
            LimitationRow(label = "WHAT IT CAN ACTUALLY DO:", content = whatItCanDo, color = Color(0xFF34D399))
            LimitationRow(label = "LIMITATION:", content = limitation, color = Color(0xFFF87171))
            LimitationRow(label = "OUR FALLBACK:", content = fallback, color = Color(0xFFFBBF24))
        }
    }
}

@Composable
fun LimitationRow(label: String, content: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF64748B)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            lineHeight = 18.sp
        )
    }
}
