package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ProtectionMode

@Composable
fun FirstRunSetupScreen(
    onCompleteSetup: (mode: ProtectionMode, pin: String, durationDays: Int) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedMode by remember { mutableStateOf(ProtectionMode.COMMITMENT_LOCK) }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var durationDays by remember { mutableStateOf(30) }
    var pinError by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize().testTag("onboarding_surface"),
        color = Color(0xFF090D16)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF0284C7).copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "BULLDOG SHIELD",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    ),
                    color = Color(0xFFF8FAFC)
                )

                Text(
                    text = "Engineering MVP • Privacy-First Offline Blocker",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF38BDF8)
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (step) {
                    1 -> {
                        // Step 1: Purpose & Privacy explanation
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Karibu kwenye Bulldog",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFF8FAFC)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Lengo kuu ni kukusaidia kuepuka picha na video chafu za ngono (pornography) kwa kutumia mfumo imara unaofanya kazi 100% bila mtandao (offline-first).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "🔒 Kanuni Kuu ya Faragha (Privacy):",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                PrivacyBulletPoint("App haitumiki kama keylogger.")
                                PrivacyBulletPoint("Hakuna maneno, nenosiri, au faili zinazotumwa kwenye seva.")
                                PrivacyBulletPoint("Ulinzi unafanyika kwenye simu yako pekee kupitia Room Database.")
                            }
                        }
                    }

                    2 -> {
                        // Step 2: Protection Mode Selection
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Chagua Kiwango cha Ulinzi",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFF8FAFC)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                ModeOptionCard(
                                    title = "COMMITMENT LOCK (Inapendekezwa)",
                                    desc = "Ulinzi mkali. Settings zinafungwa kwa muda uliouchagua. Kufungua kabla ya muda kunahitaji kusubiri masaa 24 (cooling-off period).",
                                    isSelected = selectedMode == ProtectionMode.COMMITMENT_LOCK,
                                    onClick = { selectedMode = ProtectionMode.COMMITMENT_LOCK }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                ModeOptionCard(
                                    title = "PROTECTED",
                                    desc = "Ulinzi unafanya kazi, kubadili mipangilio kunahitaji kuingiza PIN.",
                                    isSelected = selectedMode == ProtectionMode.PROTECTED,
                                    onClick = { selectedMode = ProtectionMode.PROTECTED }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                ModeOptionCard(
                                    title = "NORMAL",
                                    desc = "Ulinzi unawashwa lakini mipangilio inabaki wazi kubadilika bila PIN.",
                                    isSelected = selectedMode == ProtectionMode.NORMAL,
                                    onClick = { selectedMode = ProtectionMode.NORMAL }
                                )
                            }
                        }
                    }

                    3 -> {
                        // Step 3: PIN Setup & Lock Duration
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Unda PIN na Muda wa Ahadi",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFF8FAFC)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                if (selectedMode == ProtectionMode.COMMITMENT_LOCK) {
                                    Text(
                                        text = "Chagua Siku za Ahadi (Commitment Duration):",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(1, 7, 30, 90).forEach { d ->
                                            FilterChip(
                                                selected = durationDays == d,
                                                onClick = { durationDays = d },
                                                label = { Text("${d} Siku") }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                OutlinedTextField(
                                    value = pin,
                                    onValueChange = {
                                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                                            pin = it
                                            pinError = null
                                        }
                                    },
                                    label = { Text("Weka PIN (Namba 4-6)") },
                                    modifier = Modifier.fillMaxWidth().testTag("onboarding_pin_input"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF475569)
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = confirmPin,
                                    onValueChange = {
                                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                                            confirmPin = it
                                            pinError = null
                                        }
                                    },
                                    label = { Text("Rudia PIN") },
                                    modifier = Modifier.fillMaxWidth().testTag("onboarding_confirm_pin_input"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF475569)
                                    )
                                )

                                pinError?.let { err ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = err, color = Color(0xFFEF4444), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation buttons
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            // Validate PIN
                            if (pin.length < 4) {
                                pinError = "PIN lazima iwe angalau namba 4."
                            } else if (pin != confirmPin) {
                                pinError = "PIN hazilingani."
                            } else {
                                onCompleteSetup(selectedMode, pin, durationDays)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text(
                        text = if (step == 3) "Washa Ulinzi wa Bulldog" else "Endelea",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ModeOptionCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFF0F172A),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8)) else null
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) Color(0xFF38BDF8) else Color(0xFFF1F5F9)
                )
                if (isSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF38BDF8))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
        }
    }
}
