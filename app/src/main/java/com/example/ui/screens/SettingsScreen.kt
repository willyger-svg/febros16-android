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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ProtectionMode
import com.example.ui.BulldogUiState

@Composable
fun SettingsScreen(
    uiState: BulldogUiState,
    onToggleSetting: (key: String, value: Boolean) -> Unit
) {
    val settings = uiState.settings
    val lock = uiState.commitmentLock
    val isStrictLocked = lock != null && lock.enabled && lock.mode == ProtectionMode.COMMITMENT_LOCK.name && System.currentTimeMillis() < lock.unlockAvailableAt

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen_lazy_column")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warning Banner if Locked
        if (isStrictLocked) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🔒 Protected Settings Locked",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFCA5A5)
                            )
                            Text(
                                text = "Mipangilio hii imefungwa na Commitment Lock. Huwezi kuizima hadi muda wa ahadi umalizike.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }
        }

        // Section: Protected Settings
        item {
            Text(
                text = "PROTECTED SETTINGS (MIPANGILIO ILIYOLINDWA)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kubadilisha mipangilio hii kunahitaji PIN au kungojea Commitment Lock ipite.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingToggleRow(
                        title = "File Name Protection",
                        subtitle = "Kagua majina ya faili zote mpya",
                        isChecked = settings?.fileProtectionEnabled ?: true,
                        isLocked = isStrictLocked,
                        onCheckedChange = { onToggleSetting("file", it) }
                    )

                    SettingToggleRow(
                        title = "Search & Text Filtering",
                        subtitle = "Chuja maneno kwenye injini ya maneno",
                        isChecked = settings?.searchFilteringEnabled ?: true,
                        isLocked = isStrictLocked,
                        onCheckedChange = { onToggleSetting("search", it) }
                    )

                    SettingToggleRow(
                        title = "Custom Secure Keyboard",
                        subtitle = "Kagua na kuzuia maneno yanapoandikwa",
                        isChecked = settings?.keyboardProtectionEnabled ?: true,
                        isLocked = isStrictLocked,
                        onCheckedChange = { onToggleSetting("keyboard", it) }
                    )

                    SettingToggleRow(
                        title = "Network & DNS Rules",
                        subtitle = "Zuia tovuti na majina ya vikoa vya ngono",
                        isChecked = settings?.networkProtectionEnabled ?: true,
                        isLocked = isStrictLocked,
                        onCheckedChange = { onToggleSetting("network", it) }
                    )

                    SettingToggleRow(
                        title = "Anti-Tamper & App Protection",
                        subtitle = "Zuia kuzima au kufuta app kizembe",
                        isChecked = settings?.appProtectionEnabled ?: true,
                        isLocked = isStrictLocked,
                        onCheckedChange = { onToggleSetting("app", it) }
                    )
                }
            }
        }

        // Section: Normal Settings
        item {
            Text(
                text = "NORMAL SETTINGS (MIPANGILIO YA KAWAIDA)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = Color(0xFF94A3B8)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingToggleRow(
                        title = "Strict Offline Enforcement",
                        subtitle = "Usimamizi kamili bila kuhitaji intaneti",
                        isChecked = settings?.strictMode ?: true,
                        isLocked = false,
                        onCheckedChange = { onToggleSetting("strict", it) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    isLocked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFFF8FAFC)
                )
                if (isLocked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0284C7),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFF334155)
            )
        )
    }
}
