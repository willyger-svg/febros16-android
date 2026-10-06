package com.example.keyboard

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.database.BulldogDatabase
import com.example.keywordengine.KeywordEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Custom Secure Android Keyboard (InputMethodService).
 *
 * PRIVACY GUARANTEE:
 * - 100% Offline execution.
 * - Zero network access or external telemetry.
 * - Zero long-term storage of user keystrokes.
 * - Local-only buffer evaluation against the offline [KeywordEngine].
 */
class BulldogKeyboardService : InputMethodService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var keywordEngine: KeywordEngine
    private val typedBuffer = StringBuilder()

    private var statusBanner: TextView? = null
    private var warningBanner: TextView? = null

    override fun onCreate() {
        super.onCreate()
        keywordEngine = KeywordEngine()
        // Load custom database keywords asynchronously
        serviceScope.launch(Dispatchers.IO) {
            try {
                val db = BulldogDatabase.getInstance(applicationContext)
                val keywords = db.keywordDao().getAllEnabledKeywordsList()
                if (keywords.isNotEmpty()) {
                    keywordEngine.updateKeywords(keywords)
                }
            } catch (_: Exception) {
                // Seed dictionary already initialized
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        typedBuffer.clear()
        hideWarning()
    }

    override fun onCreateInputView(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF0F172A.toInt()) // Deep slate background
            setPadding(8, 8, 8, 16)
        }

        // 1. Protection Banner Header
        statusBanner = TextView(this).apply {
            text = "🛡 Bulldog Secure Keyboard • Pure Offline Protection"
            setTextColor(0xFF38BDF8.toInt()) // Cyan-sky blue
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(0, 4, 0, 4)
        }
        root.addView(statusBanner)

        // 2. Warning Alert Banner (Hidden by default)
        warningBanner = TextView(this).apply {
            text = "⚠️ Restricted adult search term blocked!"
            setTextColor(0xFFEF4444.toInt()) // Red alert
            setBackgroundColor(0x33EF4444.toInt())
            textSize = 13f
            gravity = Gravity.CENTER
            setPadding(8, 6, 8, 6)
            visibility = View.GONE
        }
        root.addView(warningBanner)

        // 3. Keyboard Rows (QWERTY layout)
        val rows = listOf(
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
            listOf("z", "x", "c", "v", "b", "n", "m")
        )

        for (rowKeys in rows) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                weightSum = rowKeys.size.toFloat()
            }
            for (key in rowKeys) {
                val btn = createKeyButton(key) {
                    handleKeyTyped(key)
                }
                val params = LinearLayout.LayoutParams(0, 130, 1f).apply {
                    setMargins(3, 4, 3, 4)
                }
                rowLayout.addView(btn, params)
            }
            root.addView(rowLayout)
        }

        // 4. Function Row (Space, Delete, Enter)
        val actionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            weightSum = 10f
        }

        // Backspace
        val delBtn = createKeyButton("⌫", 0xFF334155.toInt()) {
            handleBackspace()
        }
        val delParams = LinearLayout.LayoutParams(0, 130, 2f).apply {
            setMargins(3, 4, 3, 4)
        }
        actionRow.addView(delBtn, delParams)

        // Space
        val spaceBtn = createKeyButton("Space", 0xFF1E293B.toInt()) {
            handleKeyTyped(" ")
        }
        val spaceParams = LinearLayout.LayoutParams(0, 130, 6f).apply {
            setMargins(3, 4, 3, 4)
        }
        actionRow.addView(spaceBtn, spaceParams)

        // Enter
        val enterBtn = createKeyButton("↵", 0xFF0284C7.toInt()) {
            handleEnter()
        }
        val enterParams = LinearLayout.LayoutParams(0, 130, 2f).apply {
            setMargins(3, 4, 3, 4)
        }
        actionRow.addView(enterBtn, enterParams)

        root.addView(actionRow)

        return root
    }

    private fun createKeyButton(label: String, bgColor: Int = 0xFF1E293B.toInt(), onClick: () -> Unit): Button {
        return Button(this).apply {
            text = label
            setTextColor(0xFFF8FAFC.toInt())
            setBackgroundColor(bgColor)
            textSize = 18f
            isAllCaps = false
            setOnClickListener {
                vibrateKey()
                onClick()
            }
        }
    }

    private fun handleKeyTyped(char: String) {
        typedBuffer.append(char)
        val currentText = typedBuffer.toString()

        // Local-only evaluation
        val match = keywordEngine.evaluate(currentText)

        if (match.isBlocked) {
            showWarning("⚠️ Blocked adult keyword: '${match.matchedWord ?: "restricted"}'")
            // Remove the character from the buffer so the blocked word isn't formed
            if (typedBuffer.isNotEmpty()) {
                typedBuffer.deleteCharAt(typedBuffer.length - 1)
            }
            // Do NOT commit the prohibited character to third-party search field
            vibrateAlert()
        } else {
            hideWarning()
            currentInputConnection?.commitText(char, 1)
            // If space or punctuation typed, reset buffer to keep memory minimal
            if (char == " " || typedBuffer.length > 80) {
                typedBuffer.clear()
            }
        }
    }

    private fun handleBackspace() {
        if (typedBuffer.isNotEmpty()) {
            typedBuffer.deleteCharAt(typedBuffer.length - 1)
        }
        hideWarning()
        currentInputConnection?.deleteSurroundingText(1, 0)
    }

    private fun handleEnter() {
        typedBuffer.clear()
        hideWarning()
        currentInputConnection?.sendKeyEvent(
            android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER)
        )
        currentInputConnection?.sendKeyEvent(
            android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_ENTER)
        )
    }

    private fun showWarning(message: String) {
        warningBanner?.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideWarning() {
        warningBanner?.visibility = View.GONE
    }

    private fun vibrateKey() {
        val v = getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun vibrateAlert() {
        val v = getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
