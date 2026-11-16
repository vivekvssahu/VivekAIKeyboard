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
import com.vivek.aikeyboard.core.Bridge

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = 1; setPadding(28, 36, 28, 28); setBackgroundColor(0xFF101827.toInt()) }
        fun text(s: String, size: Float, color: Int = 0xFFFFFFFF.toInt(), bold: Boolean = false) = TextView(this).apply { text=s; textSize=size; setTextColor(color); if (bold) setTypeface(null, Typeface.BOLD); setPadding(0,8,0,8) }
        root.addView(text("Vivek AI Keyboard", 28f, bold=true))
        root.addView(text("AI replies using the ChatGPT app already installed on your phone. No API key is used.", 15f, 0xFFB9C4D5.toInt()))
        root.addView(text("Setup", 20f, bold=true))
        val enableIme = Button(this).apply { text="1. Enable keyboard"; setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) } }
        root.addView(enableIme, match())
        val chooseIme = Button(this).apply { text="2. Select Vivek AI Keyboard"; setOnClickListener { (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() } }
        root.addView(chooseIme, match())
        val access = Button(this).apply { text="3. Enable ChatGPT helper (Accessibility)"; setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) } }
        root.addView(access, match())
        val test = Button(this).apply { text="4. Test ChatGPT connection"; setOnClickListener { val i=packageManager.getLaunchIntentForPackage("com.openai.chatgpt"); if(i!=null) startActivity(i) else Toast.makeText(this@MainActivity,"Install the official ChatGPT app first",Toast.LENGTH_LONG).show() } }
        root.addView(test, match())
        root.addView(text("How it works", 20f, bold=true))
        root.addView(text("In a chat, open this keyboard and tap Smart Reply, Pickup, or Translate. The helper opens ChatGPT, enters a prompt and attempts to collect its visible answer. Return to your chat and tap Insert result.", 14f, 0xFFB9C4D5.toInt()))
        root.addView(text("Privacy: The accessibility helper can inspect visible ChatGPT screen content while enabled. Do not use it for passwords, banking, or sensitive conversations. Replies are drafts; you decide whether to send them.", 13f, 0xFFFFD18A.toInt()))
        root.addView(text("Bridge status: ${Bridge.status(this)}", 13f, 0xFF8FE0B0.toInt()))
        setContentView(ScrollView(this).apply { addView(root) })
    }
    private fun match() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin=8; bottomMargin=8 }
}
