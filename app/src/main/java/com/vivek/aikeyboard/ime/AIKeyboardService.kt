package com.vivek.aikeyboard.ime

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import com.vivek.aikeyboard.core.Bridge

class AIKeyboardService : InputMethodService() {
    private var style = "natural and conversational"
    private lateinit var status: TextView
    override fun onCreateInputView(): View {
        val outer = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(8,8,8,8); setBackgroundColor(Color.rgb(24,31,45)) }
        val top = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        val brand = TextView(this).apply { text="VIVEK AI"; textSize=14f; setTextColor(Color.WHITE); typeface=Typeface.DEFAULT_BOLD; setPadding(8,8,12,8) }
        top.addView(brand)
        status = TextView(this).apply { text="Choose an AI action"; textSize=11f; setTextColor(0xFFB9C4D5.toInt()) }
        top.addView(status, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(action("Insert result") { insertResult() })
        outer.addView(top)
        val styles = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        listOf("Natural","Flirty","Funny","Mature","Confident").forEach { label -> styles.addView(action(label) { style=when(label){"Flirty"->"playful and lightly flirty";"Funny"->"witty and funny";"Mature"->"mature, thoughtful and respectful";"Confident"->"confident but not arrogant";else->"natural and conversational"}; status.text="$label style selected" }) }
        outer.addView(ScrollView(this).apply { isHorizontalScrollBarEnabled=false; addView(styles) }, LinearLayout.LayoutParams(-1, -2))
        val actions = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        actions.addView(action("Smart Reply") { request("reply") }, weight())
        actions.addView(action("Pickup Line") { request("pickup") }, weight())
        actions.addView(action("Translate") { request("translate") }, weight())
        outer.addView(actions)
        val keys = listOf("qwertyuiop","asdfghjkl","zxcvbnm")
        keys.forEach { line ->
            val row=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER }
            line.forEach { ch -> row.addView(key(ch.toString()), LinearLayout.LayoutParams(0, dp(42), 1f).apply { setMargins(2,2,2,2) }) }
            outer.addView(row)
        }
        val bottom=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        bottom.addView(action("?123") { currentInputConnection?.commitText("?",1) })
        bottom.addView(action("Space") { currentInputConnection?.commitText(" ",1) }, weight())
        bottom.addView(action("⌫") { currentInputConnection?.deleteSurroundingText(1,0) })
        bottom.addView(action("↵") { currentInputConnection?.commitText("\n",1) })
        outer.addView(bottom)
        return outer
    }
    private fun action(label:String, click:()->Unit): Button = Button(this).apply { text=label; textSize=10f; isAllCaps=false; setPadding(4,2,4,2); setOnClickListener { click() } }
    private fun key(value:String): Button = Button(this).apply { text=value; textSize=17f; isAllCaps=false; setPadding(0,0,0,0); setOnClickListener { currentInputConnection?.commitText(value,1) } }
    private fun weight() = LinearLayout.LayoutParams(0,-2,1f).apply { setMargins(2,0,2,0) }
    private fun dp(v:Int)= (v*resources.displayMetrics.density).toInt()
    private fun request(kind:String) {
        val ic=currentInputConnection
        val selected=ic?.getSelectedText(0)?.toString()?.trim().orEmpty()
        val before=ic?.getTextBeforeCursor(700,0)?.toString()?.trim().orEmpty()
        val context=if(selected.isNotBlank()) selected else before
        val prompt=when(kind){
            "pickup" -> "Write 5 original, natural pickup-line options for this conversation. Style: $style. Keep them respectful, concise and not cheesy. Conversation context: ${context.ifBlank { "No context; create versatile openers." }}"
            "translate" -> "Translate the following text. Detect its language and translate to ${if(context.any { it.code > 127 }) "English" else "Hindi"}. Return only the translation: ${context.ifBlank { "Please ask me to select or type text to translate." }}"
            else -> "Suggest 5 distinct replies to this conversation. Style: $style. Make them natural, concise and context-aware. Return numbered options only. Conversation context: ${context.ifBlank { "No message context is available. Give me a short friendly conversation opener." }}"
        }
        Bridge.savePrompt(this,prompt)
        status.text="Opening ChatGPT…"
        val launch=packageManager.getLaunchIntentForPackage("com.openai.chatgpt")
        if(launch==null){ status.text="Install ChatGPT first"; return }
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launch)
    }
    private fun insertResult() {
        val result=Bridge.result(this)
        if(result.isNullOrBlank()) { status.text="No result yet. Complete the ChatGPT step first."; return }
        currentInputConnection?.commitText(result,1)
        Bridge.clearResult(this)
        status.text="Draft inserted — review before sending"
    }
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info,restarting)
        if(::status.isInitialized) status.text=if(Bridge.result(this)!=null) "Reply ready — tap Insert result" else "Choose an AI action"
    }
}
