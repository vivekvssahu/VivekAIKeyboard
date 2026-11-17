package com.vivek.aikeyboard.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.*
import android.graphics.Typeface
import android.view.ViewGroup

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 36, 28, 28)
            setBackgroundColor(0xFF101827.toInt())
        }
        fun text(s: String, size: Float, color: Int = 0xFFFFFFFF.toInt(), bold: Boolean = false) =
            TextView(this).apply {
                text = s; textSize = size; setTextColor(color)
                if (bold) setTypeface(null, Typeface.BOLD)
                setPadding(0, 8, 0, 8)
            }
        root.addView(text("Vivek AI Keyboard", 28f, bold = true))
        root.addView(text("A familiar QWERTY keyboard with local word suggestions, pickup-line drafts and emoji shortcuts.", 15f, 0xFFB9C4D5.toInt()))
        root.addView(text("Setup", 20f, bold = true))
        root.addView(Button(this).apply {
            text = "1. Enable keyboard"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        }, match())
        root.addView(Button(this).apply {
            text = "2. Select Vivek AI Keyboard"
            setOnClickListener { (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() }
        }, match())
        root.addView(text("Features & limitations", 20f, bold = true))
        root.addView(text(
            "• Standard QWERTY rows and familiar key positions\n" +
            "• Backspace at the right of the third row\n" +
            "• ?123, comma, emoji, space, period and Enter on the bottom row\n" +
            "• Local word suggestions while typing\n" +
            "• Tap Pickup line ✨ to show selectable drafts with emojis\n\n" +
            "This build does not open ChatGPT. Pickup lines and quick replies are local suggestions, not ChatGPT-generated AI. " +
            "The ChatGPT Android app has no supported silent background interface for third-party keyboards.",
            14f, 0xFFB9C4D5.toInt()
        ))
        setContentView(ScrollView(this).apply { addView(root) })
    }
    private fun match() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
        topMargin = 8; bottomMargin = 8
    }
}
