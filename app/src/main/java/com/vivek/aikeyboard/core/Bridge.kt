package com.vivek.aikeyboard.core

import android.content.Context

object Bridge {
    private const val PREF = "vivek_ai_bridge"
    private const val PROMPT = "prompt"
    private const val RESULT = "result"
    private const val STATUS = "status"
    fun savePrompt(c: Context, prompt: String) = prefs(c).edit().putString(PROMPT, prompt).putString(STATUS, "waiting").apply()
    fun prompt(c: Context): String? = prefs(c).getString(PROMPT, null)
    fun saveResult(c: Context, result: String) = prefs(c).edit().putString(RESULT, result).putString(STATUS, "ready").apply()
    fun result(c: Context): String? = prefs(c).getString(RESULT, null)
    fun clearResult(c: Context) = prefs(c).edit().remove(RESULT).remove(PROMPT).putString(STATUS, "idle").apply()
    fun status(c: Context): String = prefs(c).getString(STATUS, "idle") ?: "idle"
    private fun prefs(c: Context) = c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
}
