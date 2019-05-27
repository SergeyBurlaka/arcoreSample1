package com.jellyworkz.arstud

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import com.jellyworkz.arstud.L.TAG


/**
 * Created by BRcJU
 * @since 24.05.2019
 */

class PointerDrawable : Drawable() {
    private val paint = Paint().apply {
        textSize =35F
    }
    var enabled: Boolean = false
    override fun draw(canvas: Canvas) {
        val cx = (bounds.width() / 2).toFloat()
        val cy = (bounds.height() / 2).toFloat()
        if (enabled) {
            paint.color = Color.GREEN
            Log.d(TAG, "on draw green")
            canvas.drawCircle(cx, cy, 25F, paint)
        } else {
            paint.color = Color.RED
            Log.d(TAG, "on draw red")
            canvas.drawText("X У Й", cx, cy, paint)
        }
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

}