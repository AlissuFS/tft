package com.tftcoach

import android.content.Context
import android.graphics.*
import android.util.Log

object TemplateMatcher {

    private val augRegions = listOf(
        RectF(0.15f, 0.30f, 0.38f, 0.42f),
        RectF(0.40f, 0.30f, 0.63f, 0.42f),
        RectF(0.66f, 0.30f, 0.89f, 0.42f)
    )

    private fun loadTemplates(ctx: Context): Map<String, Bitmap> {
        val ids = mapOf(
            "celestial_blessing" to R.drawable.tpl_celestial_blessing,
            "second_wind" to R.drawable.tpl_second_wind,
            "thrill_of_the_hunt" to R.drawable.tpl_thrill_of_the_hunt
        )
        val opts = BitmapFactory.Options().apply { inScaled = true }
        return ids.map { it.key to BitmapFactory.decodeResource(ctx.resources, it.value, opts) }.toMap()
    }

    fun processFrame(ctx: Context, frame: Bitmap) {
        val templates = loadTemplates(ctx)
        val offered = mutableListOf<String>()
        augRegions.forEach { rf ->
            val r = Rect(
                (rf.left * frame.width).toInt(),
                (rf.top * frame.height).toInt(),
                (rf.right * frame.width).toInt(),
                (rf.bottom * frame.height).toInt()
            )
            if (r.width() <= 0 || r.height() <= 0) return@forEach
            val crop = Bitmap.createBitmap(frame, r.left, r.top, r.width(), r.height())
            val name = matchBest(crop, templates)
            if (name != null) offered.add(name)
        }
        if (offered.isNotEmpty()) {
            val best = recommend(offered)
            OverlayUpdater.push(ctx, "Augments: ${offered.joinToString()}  • Melhor: $best")
        }
    }

    private fun matchBest(region: Bitmap, templates: Map<String, Bitmap>): String? {
        var bestScore = -1.0; var bestName: String? = null
        val regionGray = toGray(resize(region, 160, 160))
        for ((name, tplBmp) in templates) {
            val tpl = toGray(resize(tplBmp, 80, 80))
            val score = ncc(regionGray, tpl)
            if (score > bestScore) { bestScore = score; bestName = name }
        }
        return if (bestScore > 0.55) bestName else null
    }

    private fun resize(bmp: Bitmap, w: Int, h: Int): Bitmap =
        Bitmap.createScaledBitmap(bmp, w, h, true)

    private fun toGray(bmp: Bitmap): Bitmap {
        val out = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(out)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bmp, 0f, 0f, paint)
        return out
    }

    private fun ncc(region: Bitmap, template: Bitmap): Double {
        val rw = region.width; val rh = region.height
        val tw = template.width; val th = template.height
        val sx = (rw - tw) / 2; val sy = (rh - th) / 2
        val pixelsR = IntArray(tw*th); val pixelsT = IntArray(tw*th)
        region.getPixels(pixelsR, 0, tw, sx, sy, tw, th)
        template.getPixels(pixelsT, 0, tw, 0, 0, tw, th)
        var sumR=0.0; var sumT=0.0; var sumR2=0.0; var sumT2=0.0; var sumRT=0.0
        for (i in 0 until tw*th) {
            val r = pixelsR[i] and 0xFF; val t = pixelsT[i] and 0xFF
            sumR += r; sumT += t; sumR2 += r*r; sumT2 += t*t; sumRT += r*t
        }
        val n = (tw*th).toDouble()
        val num = n*sumRT - sumR*sumT
        val den = kotlin.math.sqrt((n*sumR2 - sumR*sumR) * (n*sumT2 - sumT*sumT))
        return if (den == 0.0) -1.0 else num/den
    }

    private fun recommend(offered: List<String>): String {
        val tier = mapOf("celestial_blessing" to 3, "second_wind" to 2, "thrill_of_the_hunt" to 2)
        return offered.maxByOrNull { tier[it] ?: 0 } ?: offered.first()
    }
}
