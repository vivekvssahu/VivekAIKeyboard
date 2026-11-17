package com.vivek.aikeyboard.ime

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView

class AIKeyboardService : InputMethodService() {
    private lateinit var suggestionRow: LinearLayout
    private lateinit var status: TextView
    private var pickupMode = false
    private var shifted = false

    private val pickupLines = listOf(
        "I was going to think of a clever opener, but your smile distracted me 😊",
        "Quick question: are you always this interesting, or did I catch you on a good day? 😉",
        "I had a good opening line ready… then I saw your profile and forgot it 😅",
        "You seem like someone worth getting to know. How's your day going? ✨",
        "Your vibe caught my attention. What's one thing I should know about you? 🌻",
        "What's something that can make you smile instantly? Asking for research 😄"
    )
    private val dictionary = listOf(
        "the","there","then","they","this","that","thanks","thank","you","your","you're",
        "hello","hey","have","how","what","when","where","with","would","could","should",
        "good","great","going","got","just","nice","really","sounds","sure","okay","maybe"
    )

    override fun onCreateInputView(): android.view.View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(3))
            background = shape(0xFF202631.toInt(), 10)
        }
        suggestionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        root.addView(suggestionRow, LinearLayout.LayoutParams(-1, dp(40)))

        val tools = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        tools.addView(tool("Pickup line ✨", true) {
            pickupMode = true
            showChoices(pickupLines.take(3))
            status.text = "Tap a line to insert it"
        }, weight(1f))
        tools.addView(tool("Reply", false) {
            pickupMode = true
            showChoices(listOf("Hey! 😊", "That sounds interesting! Tell me more 🙂", "Haha, I like your sense of humour 😄"))
            status.text = "Choose a quick reply"
        }, weight(.65f))
        tools.addView(tool("Emoji", false) {
            pickupMode = true
            showChoices(listOf("😊", "😉", "😂", "❤️", "✨", "🥰"))
            status.text = "Tap an emoji to insert"
        }, weight(.55f))
        root.addView(tools, LinearLayout.LayoutParams(-1, dp(34)))

        status = TextView(this).apply {
            text = "Word suggestions"
            textSize = 9f
            gravity = Gravity.CENTER
            setTextColor(0xFF9BA8B8.toInt())
        }
        root.addView(status, LinearLayout.LayoutParams(-1, dp(14)))

        addLetters(root, "qwertyuiop")
        addLetters(root, "asdfghjkl", 14)
        addThirdRow(root)
        addBottomRow(root)
        refreshSuggestions()
        return root
    }

    private fun addLetters(root: LinearLayout, letters: String, inset: Int = 0) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(inset), dp(2), dp(inset), dp(2))
        }
        letters.forEach { c ->
            row.addView(key(if (shifted) c.uppercaseChar().toString() else c.toString(), 0xFF414B5A.toInt(), 19f) {
                currentInputConnection?.commitText(if (shifted) c.uppercaseChar().toString() else c.toString(), 1)
                shifted = false
                pickupMode = false
                refreshSuggestions()
            }, LinearLayout.LayoutParams(0, dp(42), 1f).apply { setMargins(dp(2), 0, dp(2), 0) })
        }
        root.addView(row, LinearLayout.LayoutParams(-1, dp(46)))
    }

    private fun addThirdRow(root: LinearLayout) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        row.addView(key("⇧", 0xFF596779.toInt(), 18f) {
            shifted = !shifted
            status.text = if (shifted) "Shift enabled" else "Shift disabled"
        }, fixed(42, 42))
        "zxcvbnm".forEach { c ->
            row.addView(key(if (shifted) c.uppercaseChar().toString() else c.toString(), 0xFF414B5A.toInt(), 19f) {
                currentInputConnection?.commitText(if (shifted) c.uppercaseChar().toString() else c.toString(), 1)
                shifted = false
                pickupMode = false
                refreshSuggestions()
            }, LinearLayout.LayoutParams(0, dp(42), 1f).apply { setMargins(dp(2), 0, dp(2), 0) })
        }
        row.addView(key("⌫", 0xFF596779.toInt(), 19f) {
            currentInputConnection?.deleteSurroundingText(1, 0)
            refreshSuggestions()
        }, fixed(48, 42))
        root.addView(row, LinearLayout.LayoutParams(-1, dp(46)))
    }

    private fun addBottomRow(root: LinearLayout) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        row.addView(key("?123", 0xFF596779.toInt(), 12f) { showChoices(listOf("1","2","3","4","5","6","7","8","9","0")) }, fixed(48, 43))
        row.addView(key(",", 0xFF414B5A.toInt(), 17f) { currentInputConnection?.commitText(",", 1) }, fixed(34, 43))
        row.addView(key("😊", 0xFF414B5A.toInt(), 17f) { currentInputConnection?.commitText("😊", 1) }, fixed(38, 43))
        row.addView(key("English (US)", 0xFF414B5A.toInt(), 11f) {
            currentInputConnection?.commitText(" ", 1)
            refreshSuggestions()
        }, LinearLayout.LayoutParams(0, dp(43), 1f).apply { setMargins(dp(2), 0, dp(2), 0) })
        row.addView(key(".", 0xFF414B5A.toInt(), 17f) { currentInputConnection?.commitText(".", 1) }, fixed(34, 43))
        row.addView(key(enterLabel(), 0xFF3978D4.toInt(), 18f) {
            val ic = currentInputConnection ?: return@key
            val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
            if (action != null && action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED)
                ic.performEditorAction(action) else ic.commitText("\n", 1)
        }, fixed(46, 43))
        root.addView(row, LinearLayout.LayoutParams(-1, dp(47)))
    }

    private fun enterLabel() = when (currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)) {
        EditorInfo.IME_ACTION_GO -> "Go"
        EditorInfo.IME_ACTION_SEARCH -> "⌕"
        EditorInfo.IME_ACTION_SEND -> "➤"
        EditorInfo.IME_ACTION_NEXT -> "→"
        EditorInfo.IME_ACTION_DONE -> "✓"
        else -> "↵"
    }

    private fun refreshSuggestions() {
        if (!::suggestionRow.isInitialized || pickupMode) return
        val before = currentInputConnection?.getTextBeforeCursor(40, 0)?.toString().orEmpty()
        val prefix = before.takeLastWhile { it.isLetter() }
        val matches = if (prefix.isBlank()) listOf("😊", "👍", "✨")
            else dictionary.filter { it.startsWith(prefix, true) && !it.equals(prefix, true) }.take(3)
                .ifEmpty { listOf("😊", "👍", "✨") }
        showChoices(matches)
    }

    private fun showChoices(items: List<String>) {
        if (!::suggestionRow.isInitialized) return
        suggestionRow.removeAllViews()
        items.take(3).forEach { item ->
            val v = TextView(this).apply {
                text = item
                textSize = if (item.length > 25) 10f else 14f
                maxLines = 2
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                setPadding(dp(3), 0, dp(3), 0)
                setOnClickListener {
                    currentInputConnection?.commitText(item, 1)
                    pickupMode = false
                    status.text = "Inserted — review before sending"
                    refreshSuggestions()
                }
            }
            suggestionRow.addView(v, LinearLayout.LayoutParams(0, -1, 1f))
        }
    }

    private fun key(label: String, color: Int, size: Float, click: () -> Unit) =
        TextView(this).apply {
            text = label
            textSize = size
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            maxLines = 1
            isClickable = true
            background = shape(color, 6)
            setOnClickListener { click() }
        }

    private fun tool(label: String, primary: Boolean, click: () -> Unit) =
        key(label, if (primary) 0xFF285A9E.toInt() else 0xFF354050.toInt(), 11f, click)

    private fun shape(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }
    private fun dp(n: Int) = (n * resources.displayMetrics.density).toInt()
    private fun fixed(w: Int, h: Int) = LinearLayout.LayoutParams(dp(w), dp(h)).apply { setMargins(dp(2), 0, dp(2), 0) }
    private fun weight(w: Float) = LinearLayout.LayoutParams(0, -1, w).apply { setMargins(dp(2), 0, dp(2), 0) }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        pickupMode = false
        if (::status.isInitialized) {
            status.text = "Type to see words • Pickup line for drafts"
            refreshSuggestions()
        }
    }
}
