package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keywordengine.KeywordEngine
import com.example.ui.BulldogUiState

@Composable
fun KeyboardProtectionScreen(
    uiState: BulldogUiState
) {
    val context = LocalContext.current
    var simulatedTypedText by remember { mutableStateOf("") }
    var alertBannerText by remember { mutableStateOf<String?>(null) }
    val localKeywordEngine = remember(uiState.keywords) {
        KeywordEngine(uiState.keywords)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("keyboard_screen_lazy_column")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Bulldog Secure Keyboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF8FAFC)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Kibodi maalum ya kuzuia utafutaji wa video na picha chafu kabla haujafunguka. Inafanya kazi moja kwa moja bila kutegemea intaneti au seva.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // 2. Strict Privacy Manifesto Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔒 No Keylogger Guarantee (Uhakika wa Usiri)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PrivacyBulletPoint("Hakuna maneno yanayorekodiwa au kuhifadhiwa kwenye kumbukumbu.")
                    PrivacyBulletPoint("Hakuna nenosiri (password) au ujumbe wa faragha unaotumwa popote.")
                    PrivacyBulletPoint("Huduma ya kibodi haina ruhusa ya mtandao (No internet socket).")
                    PrivacyBulletPoint("Inatumika tu kukagua kama neno lililoandikwa limo kwenye orodha ya maneno yaliyozuiliwa.")
                }
            }
        }

        // 3. Android System Enable Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How to Activate in Android Settings",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFF8FAFC)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ili kutumia kibodi hii kwenye simu yako yote:\n" +
                                "1. Fungua Settings za Android > System > Languages & Input.\n" +
                                "2. Chagua 'Manage Keyboards' kisha washa 'Bulldog Secure Keyboard'.\n" +
                                "3. Chagua Bulldog kama kibodi chaguo-msingi unapotaka ulinzi mkali.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                // Fallback
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_ime_settings_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Android Keyboard Settings", color = Color.White)
                    }
                }
            }
        }

        // 4. Interactive In-App Keyboard Sandbox Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interactive_keyboard_sandbox_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Keyboard Test Sandbox",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "Jaribu kuandika hapa chini uone jinsi kibodi inavyozuia maneno kama 'ngono' au 'porn':",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text display box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = if (simulatedTypedText.isEmpty()) "Tap keys below to type..." else simulatedTypedText,
                                color = if (simulatedTypedText.isEmpty()) Color(0xFF64748B) else Color(0xFFF8FAFC),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Warning Banner if blocked
                    alertBannerText?.let { alert ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = alert, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFCA5A5))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mini Keyboard Layout
                    val keys = listOf(
                        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
                        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
                        listOf("z", "x", "c", "v", "b", "n", "m")
                    )

                    for (row in keys) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (k in row) {
                                KeyButton(
                                    label = k,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        val testBuffer = simulatedTypedText + k
                                        val eval = localKeywordEngine.evaluate(testBuffer)
                                        if (eval.isBlocked) {
                                            alertBannerText = "⚠️ Blocked prohibited word: '${eval.matchedWord}'"
                                        } else {
                                            alertBannerText = null
                                            simulatedTypedText = testBuffer
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Space & Clear row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                simulatedTypedText = ""
                                alertBannerText = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Clear", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                simulatedTypedText += " "
                                alertBannerText = null
                            },
                            modifier = Modifier.weight(2f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Space", color = Color.White, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                if (simulatedTypedText.isNotEmpty()) {
                                    simulatedTypedText = simulatedTypedText.dropLast(1)
                                    alertBannerText = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("⌫", color = Color(0xFFEF4444), fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PrivacyBulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
    }
}

@Composable
fun KeyButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF1E293B)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = Color(0xFFF1F5F9),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
