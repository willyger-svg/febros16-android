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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.example.core.KeywordCategory
import com.example.database.KeywordEntity
import com.example.keywordengine.TextNormalizer
import com.example.ui.BulldogUiState

@Composable
fun KeywordEngineScreen(
    uiState: BulldogUiState,
    onAddKeyword: (word: String, category: String, severity: String) -> Unit,
    onDeleteKeyword: (id: Long) -> Unit
) {
    var liveInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>("ALL") }
    var isAddDialogOpen by remember { mutableStateOf(false) }

    // Real-time normalizer outputs
    val normalizedLive = remember(liveInput) { TextNormalizer.normalize(liveInput) }
    val compactLive = remember(liveInput) { TextNormalizer.normalizeCompact(liveInput) }
    val tokensLive = remember(liveInput) { TextNormalizer.extractTokens(liveInput) }

    val filteredKeywords by remember(uiState.keywords, selectedCategory) {
        derivedStateOf {
            if (selectedCategory == null || selectedCategory == "ALL") {
                uiState.keywords
            } else {
                uiState.keywords.filter { it.category == selectedCategory }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("keyword_engine_lazy_column")
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Explanation Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Offline Keyword & Normalization Engine",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFF8FAFC)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Inashughulikia herufi za siri (leetspeak kama vile p0rn, @dult), herufi zilizojirudia (poooorn), na maneno ya Kiswahili na Kiingereza bila intaneti.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // 2. Live Normalizer Interactive Inspector
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_normalizer_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Live Text Normalizer Inspector",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = liveInput,
                            onValueChange = { liveInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("normalizer_test_input"),
                            placeholder = { Text("Andika hapa kujaribu (mfano: p0oo_r.n, filamu chafu)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color(0xFFF8FAFC),
                                unfocusedTextColor = Color(0xFFE2E8F0)
                            )
                        )

                        if (liveInput.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "1. Normalized: \"$normalizedLive\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF34D399)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "2. Compact Obfuscation: \"$compactLive\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF60A5FA)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "3. Tokens: [${tokensLive.joinToString(", ")}]",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFBBF24)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Category Filter Chips
            item {
                Text(
                    text = "CATEGORIES (${filteredKeywords.size} words in database)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(6.dp))

                val categories = listOf("ALL", "SW_KISWAHILI", "ADULT_EXPLICIT", "PORN_SEARCH", "SEXUAL_ACT", "EXPLICIT_BODY", "SLANG", "MISSPELLINGS")

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.replace("_", " "), fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }

            // 4. Keyword List Items
            items(filteredKeywords, key = { it.id }) { kw ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("keyword_item_${kw.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = kw.word,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFF8FAFC)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = kw.category.replace("_", " "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF38BDF8)
                                )
                                Text(
                                    text = "• ${kw.severity}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (kw.severity) {
                                        "CRITICAL" -> Color(0xFFEF4444)
                                        "HIGH" -> Color(0xFFF97316)
                                        else -> Color(0xFFFBBF24)
                                    }
                                )
                            }
                        }

                        IconButton(
                            onClick = { onDeleteKeyword(kw.id) },
                            modifier = Modifier.testTag("delete_keyword_${kw.id}")
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

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // Add Keyword FAB
        FloatingActionButton(
            onClick = { isAddDialogOpen = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_keyword_fab"),
            containerColor = Color(0xFF0284C7),
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Keyword")
        }

        // Add Keyword Dialog
        if (isAddDialogOpen) {
            AddKeywordDialog(
                onDismiss = { isAddDialogOpen = false },
                onConfirm = { word, cat, sev ->
                    onAddKeyword(word, cat, sev)
                    isAddDialogOpen = false
                }
            )
        }
    }
}

@Composable
fun AddKeywordDialog(
    onDismiss: () -> Unit,
    onConfirm: (word: String, category: String, severity: String) -> Unit
) {
    var newWord by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ADULT_EXPLICIT") }
    var severity by remember { mutableStateOf("HIGH") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Keyword", color = Color(0xFFF8FAFC)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = newWord,
                    onValueChange = { newWord = it },
                    label = { Text("Word / Phrase") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_new_word_input"),
                    singleLine = true
                )

                Text("Category:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                val categories = listOf("ADULT_EXPLICIT", "SW_KISWAHILI", "SEXUAL_ACT", "EXPLICIT_BODY", "PORN_SEARCH", "SLANG")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.replace("_", " "), fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newWord.isNotBlank()) {
                        onConfirm(newWord, category, severity)
                    }
                },
                enabled = newWord.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier.testTag("dialog_confirm_add_keyword")
            ) {
                Text("Add to Database", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF1E293B)
    )
}
