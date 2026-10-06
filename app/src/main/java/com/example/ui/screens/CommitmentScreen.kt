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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.BulldogUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommitmentScreen(
    uiState: BulldogUiState,
    onActivateLock: (days: Int, pin: String) -> Unit,
    onRequestEarlyUnlock: () -> Unit,
    onCompleteUnlock: (pin: String) -> Unit,
    onCancelUnlock: () -> Unit
) {
    var selectedDurationDays by remember { mutableStateOf(30) }
    var pinInput by remember { mutableStateOf("") }
    var isActivateDialogOpen by remember { mutableStateOf(false) }
    var isUnlockPinDialogOpen by remember { mutableStateOf(false) }

    val lock = uiState.commitmentLock
    val unlockRequest = uiState.latestUnlockRequest
    val currentMode = try {
        ProtectionMode.valueOf(lock?.mode ?: ProtectionMode.NORMAL.name)
    } catch (_: Exception) {
        ProtectionMode.NORMAL
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("commitment_screen_lazy_column")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Philosophy & Purpose
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Commitment Lock & Self-Discipline",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF8FAFC)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mifumo mingi ya kawaida inashindwa kwa sababu mtumiaji akipata tamaa (urge), anaweza kuingia settings na kuzima ulinzi ndani ya sekunde 5.\n\n" +
                                "Bulldog Commitment Lock inakuzuia kuzima ulinzi mara moja. Hata ukitaka kufungua, lazima upitie 'Kipindi cha Kusubiri' (Cooling-off Period ya masaa 24) ili akili itulie na tamaa ipite.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // 2. Current Lock Status
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lock_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (currentMode) {
                        ProtectionMode.COMMITMENT_LOCK -> Color(0xFF0F172A)
                        ProtectionMode.PROTECTED -> Color(0xFF1E293B)
                        ProtectionMode.NORMAL -> Color(0xFF111827)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current State",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF94A3B8)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (currentMode) {
                                ProtectionMode.COMMITMENT_LOCK -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                ProtectionMode.PROTECTED -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                ProtectionMode.NORMAL -> Color(0xFF10B981).copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = currentMode.name.replace("_", " "),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when (currentMode) {
                                    ProtectionMode.COMMITMENT_LOCK -> Color(0xFFF87171)
                                    ProtectionMode.PROTECTED -> Color(0xFFFBBF24)
                                    ProtectionMode.NORMAL -> Color(0xFF34D399)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentMode == ProtectionMode.COMMITMENT_LOCK && lock != null) {
                        val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                        Text(
                            text = "Locked Until: ${dateFormat.format(Date(lock.unlockAvailableAt))}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF8FAFC)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Protected settings cannot be edited or turned off.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Unlock / Cooling-Off Section
                        if (unlockRequest != null && unlockRequest.status == "PENDING") {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.HourglassTop,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Waiting Period Active (Masaa 24)",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFFFBBF24)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val now = System.currentTimeMillis()
                                    if (now < unlockRequest.unlockReadyAt) {
                                        val remainingMillis = unlockRequest.unlockReadyAt - now
                                        val hours = remainingMillis / (1000 * 60 * 60)
                                        val mins = (remainingMillis % (1000 * 60 * 60)) / (1000 * 60)
                                        Text(
                                            text = "Remaining Time: $hours hours $mins mins\nUlinzi unaendelea kufanya kazi kikamilifu.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFFDE68A)
                                        )
                                    } else {
                                        Text(
                                            text = "Waiting period complete! You may now enter your PIN to unlock.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF34D399),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = onCancelUnlock,
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Cancel Request", color = Color(0xFFE2E8F0))
                                        }

                                        Button(
                                            onClick = { isUnlockPinDialogOpen = true },
                                            modifier = Modifier.weight(1f),
                                            enabled = now >= unlockRequest.unlockReadyAt,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Enter PIN", color = Color.White)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Show "Request Unlock" button
                            Button(
                                onClick = onRequestEarlyUnlock,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("request_unlock_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Request Unlock (Starts 24h Cooling-Off)", color = Color(0xFFE2E8F0))
                            }
                        }
                    } else {
                        Text(
                            text = "Commitment Lock is not currently activated.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { isActivateDialogOpen = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("activate_commitment_lock_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Configure & Activate Commitment Lock", color = Color.White)
                        }
                    }
                }
            }
        }

        // 3. Duration Options preview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Commitment Duration Milestones",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFF8FAFC)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Chagua kipindi cha ahadi unachotaka kujifunga nacho:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val durations = listOf(1 to "1 Day (Trial)", 7 to "7 Days", 30 to "30 Days (Recommended)", 90 to "90 Days (Reboot)")
                    durations.forEach { (days, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE2E8F0))
                            if (selectedDurationDays == days) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF0284C7).copy(alpha = 0.2f)) {
                                    Text(
                                        text = "Selected",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Activate Lock Dialog
    if (isActivateDialogOpen) {
        AlertDialog(
            onDismissRequest = { isActivateDialogOpen = false },
            title = { Text("Activate Commitment Lock", color = Color(0xFFF8FAFC)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Once confirmed, you CANNOT change protection settings until this duration ends or after a 24-hour waiting period.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFEF4444)
                    )

                    Text("Select Duration:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                    val daysOptions = listOf(1, 7, 30, 90)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        daysOptions.forEach { d ->
                            FilterChip(
                                selected = selectedDurationDays == d,
                                onClick = { selectedDurationDays = d },
                                label = { Text("${d}d") }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pinInput = it },
                        label = { Text("Create 4-6 Digit Security PIN") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activate_pin_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length >= 4) {
                            onActivateLock(selectedDurationDays, pinInput)
                            isActivateDialogOpen = false
                            pinInput = ""
                        }
                    },
                    enabled = pinInput.length >= 4,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("confirm_activate_lock_button")
                ) {
                    Text("Lock Protection", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isActivateDialogOpen = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // Unlock PIN Dialog
    if (isUnlockPinDialogOpen) {
        var verifyPin by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { isUnlockPinDialogOpen = false },
            title = { Text("Enter PIN to Complete Unlock", color = Color(0xFFF8FAFC)) },
            text = {
                OutlinedTextField(
                    value = verifyPin,
                    onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) verifyPin = it },
                    label = { Text("Security PIN") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verify_unlock_pin_input"),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCompleteUnlock(verifyPin)
                        isUnlockPinDialogOpen = false
                        verifyPin = ""
                    },
                    enabled = verifyPin.length >= 4,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Unlock", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isUnlockPinDialogOpen = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}
