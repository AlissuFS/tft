package com.tftcoach

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView

object OverlayUpdater {
    private var overlayView: View? = null
    fun attach(view: View) { overlayView = view }
    fun push(ctx: android.content.Context, text: String) {
        val v = overlayView ?: return
        val tv = v.findViewById<TextView>(R.id.overlay_text)
        Handler(Looper.getMainLooper()).post { tv.text = text }
    }
}
