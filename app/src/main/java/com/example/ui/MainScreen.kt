package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AndroidLimitationsScreen
import com.example.ui.screens.BlockedScreen
import com.example.ui.screens.CommitmentScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FileProtectionScreen
import com.example.ui.screens.FirstRunSetupScreen
import com.example.ui.screens.KeyboardProtectionScreen
import com.example.ui.screens.KeywordEngineScreen
import com.example.ui.screens.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BulldogViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotifications()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotifications()
        }
    }

    // First Run Onboarding Flow
    if (uiState.isFirstRun) {
        FirstRunSetupScreen(
            onCompleteSetup = { mode, pin, days ->
                viewModel.completeOnboarding(mode, pin, days)
            }
        )
        return
    }

    // Block Screen Overlay
    uiState.activeBlockScreen?.let { blockData ->
        BlockedScreen(
            data = blockData,
            onDismiss = { viewModel.dismissBlockScreen() }
        )
        return
    }

    // Android back navigation
    BackHandler(enabled = uiState.currentTab != NavigationTab.DASHBOARD) {
        viewModel.selectTab(NavigationTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_screen_scaffold"),
        containerColor = Color(0xFF090D16),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Bulldog Shield",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Bulldog Shield",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(0xFFF8FAFC)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color(0xFFF8FAFC)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0F172A),
                contentColor = Color(0xFF94A3B8)
            ) {
                NavigationItem(
                    tab = NavigationTab.DASHBOARD,
                    icon = Icons.Default.Shield,
                    currentTab = uiState.currentTab,
                    onSelect = { viewModel.selectTab(NavigationTab.DASHBOARD) }
                )
                NavigationItem(
                    tab = NavigationTab.KEYWORDS,
                    icon = Icons.Default.Search,
                    currentTab = uiState.currentTab,
                    onSelect = { viewModel.selectTab(NavigationTab.KEYWORDS) }
                )
                NavigationItem(
                    tab = NavigationTab.FILE_SHIELD,
                    icon = Icons.Default.Description,
                    currentTab = uiState.currentTab,
                    onSelect = { viewModel.selectTab(NavigationTab.FILE_SHIELD) }
                )
                NavigationItem(
                    tab = NavigationTab.KEYBOARD,
                    icon = Icons.Default.Keyboard,
                    currentTab = uiState.currentTab,
                    onSelect = { viewModel.selectTab(NavigationTab.KEYBOARD) }
                )
                NavigationItem(
                    tab = NavigationTab.COMMITMENT,
                    icon = Icons.Default.Lock,
                    currentTab = uiState.currentTab,
                    onSelect = { viewModel.selectTab(NavigationTab.COMMITMENT) }
                )
                NavigationItem(
                    tab = NavigationTab.LIMITATIONS,
                    icon = Icons.Default.Info,
                    currentTab = uiState.currentTab,
                    onSelect = { viewModel.selectTab(NavigationTab.LIMITATIONS) }
                )
                NavigationItem(
                    tab = NavigationTab.SETTINGS,
                    icon = Icons.Default.Settings,
                    currentTab = uiState.currentTab,
                    onSelect = { viewModel.selectTab(NavigationTab.SETTINGS) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.currentTab) {
                NavigationTab.DASHBOARD -> {
                    DashboardScreen(
                        uiState = uiState,
                        onTestQuery = { viewModel.evaluateSearchQuery(it) },
                        onTestFile = { viewModel.scanFileTest(it) },
                        onTriggerDemoBlock = {
                            viewModel.triggerBlockScreen(
                                title = "DEMO BLOCK SCREEN",
                                reason = "Demonstration of standard calm block UI without explicit content."
                            )
                        },
                        onNavigateTab = { viewModel.selectTab(it) }
                    )
                }
                NavigationTab.KEYWORDS -> {
                    KeywordEngineScreen(
                        uiState = uiState,
                        onAddKeyword = { w, c, s -> viewModel.addCustomKeyword(w, c, s) },
                        onDeleteKeyword = { id -> viewModel.deleteKeyword(id) }
                    )
                }
                NavigationTab.FILE_SHIELD -> {
                    FileProtectionScreen(
                        uiState = uiState,
                        onScanFile = { name, content -> viewModel.scanFileTest(name, content) },
                        onDeleteBlockedFile = { id -> viewModel.deleteBlockedFile(id) }
                    )
                }
                NavigationTab.KEYBOARD -> {
                    KeyboardProtectionScreen(uiState = uiState)
                }
                NavigationTab.COMMITMENT -> {
                    CommitmentScreen(
                        uiState = uiState,
                        onActivateLock = { days, pin -> viewModel.activateCommitmentLock(days, pin) },
                        onRequestEarlyUnlock = { viewModel.requestEarlyUnlock() },
                        onCompleteUnlock = { pin -> viewModel.completeWaitingPeriodUnlock(pin) },
                        onCancelUnlock = { viewModel.cancelUnlockRequest() }
                    )
                }
                NavigationTab.LIMITATIONS -> {
                    AndroidLimitationsScreen()
                }
                NavigationTab.SETTINGS -> {
                    SettingsScreen(
                        uiState = uiState,
                        onToggleSetting = { key, value -> viewModel.attemptToggleSetting(key, value) }
                    )
                }
            }
        }
    }

    // PIN Verification Dialog for Protected Settings
    if (uiState.isPinDialogOpen) {
        var pinInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.closePinDialog() },
            title = { Text("Security Authentication", color = Color(0xFFF8FAFC)) },
            text = {
                Column {
                    Text(
                        text = "Enter your security PIN to modify protected settings:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pinInput = it },
                        label = { Text("PIN") },
                        modifier = Modifier.fillMaxWidth().testTag("pin_dialog_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitPin(pinInput) },
                    enabled = pinInput.length >= 4,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.testTag("submit_pin_dialog_button")
                ) {
                    Text("Verify PIN", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closePinDialog() }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NavigationItem(
    tab: NavigationTab,
    icon: ImageVector,
    currentTab: NavigationTab,
    onSelect: () -> Unit
) {
    val isSelected = tab == currentTab
    NavigationBarItem(
        selected = isSelected,
        onClick = onSelect,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = tab.title,
                tint = if (isSelected) Color(0xFF38BDF8) else Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
        },
        label = {
            Text(
                text = tab.title,
                fontSize = 10.sp,
                color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF64748B)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color(0xFF0284C7).copy(alpha = 0.2f)
        )
    )
}
