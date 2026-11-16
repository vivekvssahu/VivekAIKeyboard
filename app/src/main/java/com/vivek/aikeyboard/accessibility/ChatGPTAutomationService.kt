package com.vivek.aikeyboard.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.os.Handler
import android.os.Looper
import android.content.Intent
import com.vivek.aikeyboard.core.Bridge

class ChatGPTAutomationService : AccessibilityService() {
    private val handler=Handler(Looper.getMainLooper())
    private var promptSubmitted=false
    private var lastCandidate=""
    private var stableCount=0
    private var promptStartedAt=0L
    private var activePrompt=""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(event?.packageName?.toString()!="com.openai.chatgpt") return
        val prompt=Bridge.prompt(this) ?: return
        if(prompt != activePrompt) { activePrompt=prompt; promptSubmitted=false; lastCandidate=""; stableCount=0 }
        if(Bridge.status(this)=="ready") return
        if(!promptSubmitted) {
            val input=findInput(rootInActiveWindow)
            if(input!=null) {
                val args=android.os.Bundle().apply { putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,prompt) }
                if(input.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,args)) {
                    promptStartedAt=System.currentTimeMillis()
                    handler.postDelayed({ clickSendIfAvailable() },700)
                    promptSubmitted=true
                }
            }
        } else {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ captureIfStable() },2500)
        }
    }
    private fun findInput(node:AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if(node==null) return null
        if(node.isEditable && node.isVisibleToUser) return node
        for(i in 0 until node.childCount) { val found=findInput(node.getChild(i)); if(found!=null) return found }
        return null
    }
    private fun clickSendIfAvailable() {
        val root=rootInActiveWindow ?: return
        val candidates=mutableListOf<AccessibilityNodeInfo>()
        collect(root,candidates)
        val send=candidates.firstOrNull { n ->
            val s=(n.text?.toString()+" "+n.contentDescription?.toString()).lowercase()
            (s.contains("send") || s.contains("submit")) && n.isEnabled
        }
        if(send!=null) send.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        else {
            // Some ChatGPT versions expose an unlabeled send icon; do not guess-click unrelated controls.
        }
        handler.postDelayed({ captureIfStable() },5000)
    }
    private fun captureIfStable() {
        val root=rootInActiveWindow ?: return
        val texts=mutableListOf<String>()
        collectText(root,texts)
        val prompt=Bridge.prompt(this).orEmpty()
        val candidate=texts.asReversed().firstOrNull { t ->
            t.length>35 && !t.contains(prompt.take(45),true) && !t.equals("ChatGPT",true)
        }.orEmpty()
        if(candidate.isBlank()) return
        if(candidate==lastCandidate) stableCount++ else { lastCandidate=candidate; stableCount=0 }
        if(stableCount>=1 && System.currentTimeMillis()-promptStartedAt>3500) {
            Bridge.saveResult(this,candidate)
            sendBroadcast(Intent("com.vivek.aikeyboard.RESULT_READY").setPackage(packageName))
        } else handler.postDelayed({ captureIfStable() },1800)
    }
    private fun collect(node:AccessibilityNodeInfo?, out:MutableList<AccessibilityNodeInfo>) {
        if(node==null) return
        out.add(node)
        for(i in 0 until node.childCount) collect(node.getChild(i),out)
    }
    private fun collectText(node:AccessibilityNodeInfo?, out:MutableList<String>) {
        if(node==null) return
        node.text?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let(out::add)
        node.contentDescription?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let(out::add)
        for(i in 0 until node.childCount) collectText(node.getChild(i),out)
    }
    override fun onInterrupt() {}
    override fun onServiceConnected() { super.onServiceConnected(); promptSubmitted=false }
}
