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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
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
fun DashboardScreen(
    uiState: BulldogUiState,
    onTestQuery: (String) -> Unit,
    onTestFile: (String) -> Unit,
    onTriggerDemoBlock: () -> Unit,
    onNavigateTab: (com.example.ui.NavigationTab) -> Unit
) {
    var testInput by remember { mutableStateOf("") }
    val lock = uiState.commitmentLock
    val settings = uiState.settings

    val mode = try {
        ProtectionMode.valueOf(lock?.mode ?: ProtectionMode.NORMAL.name)
    } catch (_: Exception) {
        ProtectionMode.NORMAL
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_lazy_column")
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Shield Status Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_shield_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (mode) {
                        ProtectionMode.COMMITMENT_LOCK -> Color(0xFF0F172A)
                        ProtectionMode.PROTECTED -> Color(0xFF111827)
                        ProtectionMode.NORMAL -> Color(0xFF0B192C)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF0284C7).copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Shield",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "🛡 PROTECTION ACTIVE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = Color(0xFFF8FAFC)
                                )
                                Text(
                                    text = "Bulldog Offline Engine • Ulinzi wa Ndani",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Mode Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (mode) {
                                ProtectionMode.COMMITMENT_LOCK -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                ProtectionMode.PROTECTED -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                ProtectionMode.NORMAL -> Color(0xFF10B981).copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = mode.name.replace("_", " "),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when (mode) {
                                    ProtectionMode.COMMITMENT_LOCK -> Color(0xFFF87171)
                                    ProtectionMode.PROTECTED -> Color(0xFFFBBF24)
                                    ProtectionMode.NORMAL -> Color(0xFF34D399)
                                }
                            )
                        }
                    }

                    // Commitment Lock Expiry or Details
                    if (lock != null && lock.enabled && lock.unlockAvailableAt > 0L) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Commitment Lock Target:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFFE2E8F0),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                                Text(
                                    text = dateFormat.format(Date(lock.unlockAvailableAt)),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Settings cannot be deactivated until this date.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Protection Layers Matrix (Section 24)
        item {
            Text(
                text = "PROTECTION LAYERS",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LayerStatusCard(
                        title = "File Protection",
                        icon = Icons.Default.Description,
                        isEnabled = settings?.fileProtectionEnabled ?: true,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(com.example.ui.NavigationTab.FILE_SHIELD) }
                    )
                    LayerStatusCard(
                        title = "Search Filtering",
                        icon = Icons.Default.Search,
                        isEnabled = settings?.searchFilteringEnabled ?: true,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(com.example.ui.NavigationTab.KEYWORDS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LayerStatusCard(
                        title = "Custom Keyboard",
                        icon = Icons.Default.Keyboard,
                        isEnabled = settings?.keyboardProtectionEnabled ?: true,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(com.example.ui.NavigationTab.KEYBOARD) }
                    )
                    LayerStatusCard(
                        title = "Network DNS",
                        icon = Icons.Default.Dns,
                        isEnabled = settings?.networkProtectionEnabled ?: true,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(com.example.ui.NavigationTab.SETTINGS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LayerStatusCard(
                        title = "App Protection",
                        icon = Icons.Default.Apps,
                        isEnabled = settings?.appProtectionEnabled ?: true,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(com.example.ui.NavigationTab.LIMITATIONS) }
                    )
                    LayerStatusCard(
                        title = "Commitment Lock",
                        icon = Icons.Default.Lock,
                        isEnabled = lock?.enabled ?: false,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(com.example.ui.NavigationTab.COMMITMENT) }
                    )
                }
            }
        }

        // 3. Quick Sandbox / Engine Testing (Try queries/file names live)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sandbox_test_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Real-Time Protection Sandbox",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFF8FAFC)
                    )
                    Text(
                        text = "Test search phrases or file names directly against the offline Keyword & File Engine:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = testInput,
                        onValueChange = { testInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sandbox_input_field"),
                        placeholder = { Text("e.g. ngono, p0rn, explicit_movie.mp4, clean_video.mp4") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color(0xFFF8FAFC),
                            unfocusedTextColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onTestQuery(testInput) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_search_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0284C7)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Test Search", color = Color.White)
                        }

                        Button(
                            onClick = { onTestFile(testInput) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_file_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0369A1)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Test File", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onTriggerDemoBlock,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("view_block_screen_demo_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Preview Block Screen UI", color = Color(0xFFE2E8F0))
                    }
                }
            }
        }

        // 4. Privacy & Offline Architecture Declaration
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("privacy_declaration_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0B132B)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Privacy",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "100% Privacy & Offline Guarantee",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF8FAFC)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Hakuna data inayotumwa kwenye seva yoyote mtandaoni. Michakato yote ya maneno, faili, na kibodi inafanyika hapa hapa kwenye simu yako kupitia Room Database na PBKDF2 cryptography.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LayerStatusCard(
    title: String,
    icon: ImageVector,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.testTag("layer_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isEnabled) Color(0xFF38BDF8) else Color(0xFF64748B),
                    modifier = Modifier.size(22.dp)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isEnabled) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF64748B).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (isEnabled) "ON" else "OFF",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isEnabled) Color(0xFF34D399) else Color(0xFF94A3B8)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFFF1F5F9)
            )
        }
    }
}
