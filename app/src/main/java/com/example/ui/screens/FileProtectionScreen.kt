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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fileprotection.FileFingerprint
import com.example.ui.BulldogUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FileProtectionScreen(
    uiState: BulldogUiState,
    onScanFile: (fileName: String, content: String) -> Unit,
    onDeleteBlockedFile: (id: Long) -> Unit
) {
    var testFileName by remember { mutableStateOf("explicit_video.mp4") }
    var testFileContent by remember { mutableStateOf("sample_adult_payload_data_binary") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("file_protection_lazy_column")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Architecture Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Offline File Name & Rename Protection",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF8FAFC)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Kuchuja Jina: Faili jipya likiingia (mfano: explicit_porn.mp4), mfumo huzuia kupitia injini ya maneno.\n" +
                                "2. Alama ya Kidole (SHA-256 Hash): Faili lililozuiwa linarekodiwa alama yake. Hata mtumiaji akibadili jina kuwa movie.mp4, mfumo bado utalitambua na kulizuia!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // 2. Interactive File Simulator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("file_simulator_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive File Scan Simulator",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = testFileName,
                        onValueChange = { testFileName = it },
                        label = { Text("File Name (e.g. video_ngono.mp4, clean.mp4)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulated_filename_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color(0xFFF8FAFC),
                            unfocusedTextColor = Color(0xFFE2E8F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = testFileContent,
                        onValueChange = { testFileContent = it },
                        label = { Text("Simulated File Content / Stream") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulated_content_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color(0xFFF8FAFC),
                            unfocusedTextColor = Color(0xFFE2E8F0)
                        )
                    )

                    val computedHash = remember(testFileContent) {
                        FileFingerprint.hashString(testFileContent)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SHA-256 Fingerprint: ${computedHash.take(16)}...",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onScanFile(testFileName, testFileContent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_file_scan_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("Scan File with Protection Engine", color = Color.White)
                    }

                    // Display last scan result
                    uiState.lastFileScan?.let { res ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (res.isBlocked) Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (res.isBlocked) "🚨 FILE BLOCKED" else "✅ FILE ALLOWED",
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.isBlocked) Color(0xFFEF4444) else Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = res.reason,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE2E8F0)
                                )
                                Text(
                                    text = "Detection Method: ${res.detectedVia}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Known Blocked Files Fingerprint Database
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BLOCKED FILE FINGERPRINTS (${uiState.blockedFiles.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF94A3B8)
                )
            }
        }

        if (uiState.blockedFiles.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "No blocked files registered yet. When an adult file is detected, its cryptographic fingerprint will be stored here to prevent renaming bypass.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(uiState.blockedFiles, key = { it.id }) { file ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("blocked_file_item_${file.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = file.originalName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFF8FAFC)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hash: ${file.fingerprint.take(24)}...",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF38BDF8),
                                fontFamily = FontFamily.Monospace
                            )
                            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            Text(
                                text = "Detected: ${dateFormat.format(Date(file.detectedAt))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteBlockedFile(file.id) },
                            modifier = Modifier.testTag("delete_blocked_file_${file.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF4444)
                            )
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
