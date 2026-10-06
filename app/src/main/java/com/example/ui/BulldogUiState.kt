package com.example.ui

import com.example.core.FileScanResult
import com.example.core.KeywordCategory
import com.example.core.KeywordMatchResult
import com.example.core.ProtectionMode
import com.example.database.BlockedFileEntity
import com.example.database.CommitmentLockEntity
import com.example.database.KeywordEntity
import com.example.database.ProtectionSettingsEntity
import com.example.database.UnlockRequestEntity
import com.example.security.SecurityManager

enum class NavigationTab(val title: String) {
    DASHBOARD("Dashboard"),
    KEYWORDS("Keywords"),
    FILE_SHIELD("File Shield"),
    KEYBOARD("Keyboard"),
    COMMITMENT("Commitment"),
    LIMITATIONS("OS Limits"),
    SETTINGS("Settings")
}

data class BlockedScreenData(
    val title: String = "CONTENT RESTRICTED",
    val reason: String = "This content is restricted by your protection settings.",
    val category: String = "Adult Explicit Protection",
    val timestamp: Long = System.currentTimeMillis()
)

data class BulldogUiState(
    val currentTab: NavigationTab = NavigationTab.DASHBOARD,
    val isInitialized: Boolean = false,
    val isFirstRun: Boolean = false,
    val settings: ProtectionSettingsEntity? = null,
    val commitmentLock: CommitmentLockEntity? = null,
    val latestUnlockRequest: UnlockRequestEntity? = null,
    val keywords: List<KeywordEntity> = emptyList(),
    val blockedFiles: List<BlockedFileEntity> = emptyList(),
    val lastSearchMatch: KeywordMatchResult? = null,
    val lastFileScan: FileScanResult? = null,
    val activeBlockScreen: BlockedScreenData? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isPinDialogOpen: Boolean = false,
    val pendingAction: PendingProtectedAction? = null,
    val clockStatus: SecurityManager.ClockIntegrityStatus = SecurityManager.ClockIntegrityStatus.OK
)

sealed class PendingProtectedAction {
    data class ToggleSetting(val key: String, val newValue: Boolean) : PendingProtectedAction()
    data class ChangeMode(val targetMode: ProtectionMode) : PendingProtectedAction()
    object OpenProtectedSettings : PendingProtectedAction()
}
