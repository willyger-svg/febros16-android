package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.commitment.CommitmentEngine
import com.example.core.FileScanResult
import com.example.core.KeywordMatchResult
import com.example.core.ProtectionMode
import com.example.database.BulldogDatabase
import com.example.database.BulldogRepository
import com.example.database.ProtectionSettingsEntity
import com.example.fileprotection.FileProtectionEngine
import com.example.keywordengine.KeywordEngine
import com.example.protection.ProtectionEngine
import com.example.security.SecurityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BulldogViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BulldogDatabase.getInstance(application)
    private val repository = BulldogRepository(db)
    private val keywordEngine = KeywordEngine()
    private val fileEngine = FileProtectionEngine(keywordEngine, db.blockedFileDao())
    val commitmentEngine = CommitmentEngine(db.commitmentDao(), db.unlockRequestDao())
    val protectionEngine = ProtectionEngine(keywordEngine, fileEngine, commitmentEngine, repository)

    private val _uiState = MutableStateFlow(BulldogUiState())
    val uiState: StateFlow<BulldogUiState> = _uiState.asStateFlow()

    init {
        initializeData()
        observeDatabaseFlows()
    }

    private fun initializeData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeDefaultsIfNeeded()
            val initialKeywords = repository.getEnabledKeywords()
            keywordEngine.updateKeywords(initialKeywords)

            val currentSettings = db.settingsDao().getSettings()
            val isFirstRun = currentSettings == null || !currentSettings.firstRunCompleted

            _uiState.update {
                it.copy(
                    isInitialized = true,
                    isFirstRun = isFirstRun
                )
            }
        }
    }

    private fun observeDatabaseFlows() {
        viewModelScope.launch {
            repository.settingsFlow.collectLatest { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }

        viewModelScope.launch {
            repository.commitmentLockFlow.collectLatest { lock ->
                _uiState.update { it.copy(commitmentLock = lock) }
            }
        }

        viewModelScope.launch {
            repository.latestUnlockRequestFlow.collectLatest { req ->
                _uiState.update { it.copy(latestUnlockRequest = req) }
            }
        }

        viewModelScope.launch {
            repository.keywordsFlow.collectLatest { kws ->
                keywordEngine.updateKeywords(kws)
                _uiState.update { it.copy(keywords = kws) }
            }
        }

        viewModelScope.launch {
            repository.blockedFilesFlow.collectLatest { files ->
                _uiState.update { it.copy(blockedFiles = files) }
            }
        }
    }

    fun selectTab(tab: NavigationTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun completeOnboarding(mode: ProtectionMode, pin: String, durationDays: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (mode) {
                ProtectionMode.NORMAL -> {
                    // Normal mode
                }
                ProtectionMode.PROTECTED -> {
                    commitmentEngine.activateProtectedMode(pin)
                }
                ProtectionMode.COMMITMENT_LOCK -> {
                    commitmentEngine.activateCommitmentLock(durationDays, pin)
                }
            }

            val currentSettings = db.settingsDao().getSettings() ?: ProtectionSettingsEntity()
            val updated = currentSettings.copy(firstRunCompleted = true)
            repository.updateSettings(updated)

            _uiState.update {
                it.copy(
                    isFirstRun = false,
                    successMessage = "Bulldog Shield initialized successfully in ${mode.name} mode"
                )
            }
        }
    }

    /**
     * Real-time testing of search / text keyword evaluation.
     */
    fun evaluateSearchQuery(query: String) {
        val result = protectionEngine.evaluateSearchText(query)
        _uiState.update { it.copy(lastSearchMatch = result) }

        if (result.isBlocked) {
            // Trigger block screen preview to illustrate protection
            triggerBlockScreen(
                title = "SEARCH QUERY BLOCKED",
                reason = "Prohibited term detected: '${result.matchedWord}' (${result.reason})"
            )
        }
    }

    /**
     * Real-time testing of file-name and cryptographic fingerprint filtering.
     */
    fun scanFileTest(fileName: String, fileContent: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val bytes = if (fileContent.isNotEmpty()) fileContent.toByteArray() else null
            val result = protectionEngine.scanFile(fileName, bytes)

            _uiState.update { it.copy(lastFileScan = result) }

            if (result.isBlocked) {
                triggerBlockScreen(
                    title = "FILE RESTRICTED",
                    reason = result.reason
                )
            }
        }
    }

    fun triggerBlockScreen(title: String, reason: String) {
        _uiState.update {
            it.copy(
                activeBlockScreen = BlockedScreenData(
                    title = title,
                    reason = reason,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun dismissBlockScreen() {
        _uiState.update { it.copy(activeBlockScreen = null) }
    }

    fun addCustomKeyword(word: String, category: String, severity: String) {
        if (word.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.addKeyword(word.trim(), category, severity)
            _uiState.update { it.copy(successMessage = "Added keyword: $word") }
        }
    }

    fun deleteKeyword(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteKeyword(id)
            _uiState.update { it.copy(successMessage = "Keyword removed") }
        }
    }

    fun deleteBlockedFile(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBlockedFile(id)
            _uiState.update { it.copy(successMessage = "File signature removed from blocked catalog") }
        }
    }

    // --- Commitment Lock Actions ---

    fun activateCommitmentLock(days: Int, pin: String) {
        viewModelScope.launch(Dispatchers.IO) {
            commitmentEngine.activateCommitmentLock(days, pin)
            _uiState.update { it.copy(successMessage = "Commitment Lock active for $days days!") }
        }
    }

    fun requestEarlyUnlock() {
        viewModelScope.launch(Dispatchers.IO) {
            val req = commitmentEngine.requestEarlyUnlock()
            if (req != null) {
                _uiState.update {
                    it.copy(
                        latestUnlockRequest = req,
                        successMessage = "Unlock cooling-off period started. Protection remains active."
                    )
                }
            }
        }
    }

    fun completeWaitingPeriodUnlock(pin: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = commitmentEngine.completeWaitingPeriodUnlock(pin)
            when (result) {
                is CommitmentEngine.UnlockResult.Success -> {
                    _uiState.update {
                        it.copy(
                            successMessage = "Unlock complete. Switched to NORMAL mode.",
                            errorMessage = null
                        )
                    }
                }
                is CommitmentEngine.UnlockResult.InvalidPin -> {
                    _uiState.update { it.copy(errorMessage = "Incorrect PIN code.") }
                }
                is CommitmentEngine.UnlockResult.WaitingPeriodNotElapsed -> {
                    val hours = result.remainingMillis / (1000 * 60 * 60)
                    val mins = (result.remainingMillis % (1000 * 60 * 60)) / (1000 * 60)
                    _uiState.update {
                        it.copy(errorMessage = "Waiting period active: $hours hours $mins mins remaining before unlock is permitted.")
                    }
                }
                is CommitmentEngine.UnlockResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }

    fun cancelUnlockRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            commitmentEngine.cancelUnlockRequest()
            _uiState.update { it.copy(successMessage = "Unlock request cancelled. Commitment lock remains active.") }
        }
    }

    fun attemptToggleSetting(key: String, targetValue: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val isAllowed = commitmentEngine.isProtectedActionAllowed()
            val lock = _uiState.value.commitmentLock

            if (lock != null && lock.enabled && lock.mode == ProtectionMode.COMMITMENT_LOCK.name) {
                val now = System.currentTimeMillis()
                if (now < lock.unlockAvailableAt) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "🔒 Settings are locked by Commitment Lock. Protection cannot be modified until commitment period ends."
                        )
                    }
                    return@launch
                }
            }

            if (lock != null && lock.enabled && lock.mode == ProtectionMode.PROTECTED.name) {
                // Prompt PIN
                _uiState.update {
                    it.copy(
                        isPinDialogOpen = true,
                        pendingAction = PendingProtectedAction.ToggleSetting(key, targetValue)
                    )
                }
                return@launch
            }

            // Normal mode
            applySettingToggle(key, targetValue)
        }
    }

    fun submitPin(pin: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val valid = commitmentEngine.verifyPin(pin)
            if (!valid) {
                _uiState.update { it.copy(errorMessage = "Incorrect PIN") }
                return@launch
            }

            val pending = _uiState.value.pendingAction
            _uiState.update { it.copy(isPinDialogOpen = false, pendingAction = null) }

            when (pending) {
                is PendingProtectedAction.ToggleSetting -> {
                    applySettingToggle(pending.key, pending.newValue)
                }
                is PendingProtectedAction.ChangeMode -> {
                    if (pending.targetMode == ProtectionMode.NORMAL) {
                        commitmentEngine.switchToNormalMode(pin)
                    }
                }
                is PendingProtectedAction.OpenProtectedSettings -> {
                    // Authorized
                }
                null -> {}
            }
        }
    }

    fun closePinDialog() {
        _uiState.update { it.copy(isPinDialogOpen = false, pendingAction = null) }
    }

    private suspend fun applySettingToggle(key: String, value: Boolean) {
        val current = _uiState.value.settings ?: ProtectionSettingsEntity()
        val updated = when (key) {
            "file" -> current.copy(fileProtectionEnabled = value)
            "search" -> current.copy(searchFilteringEnabled = value)
            "keyboard" -> current.copy(keyboardProtectionEnabled = value)
            "network" -> current.copy(networkProtectionEnabled = value)
            "app" -> current.copy(appProtectionEnabled = value)
            "strict" -> current.copy(strictMode = value)
            else -> current
        }
        repository.updateSettings(updated)
        _uiState.update { it.copy(successMessage = "Setting updated: $key = $value") }
    }

    fun clearNotifications() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
