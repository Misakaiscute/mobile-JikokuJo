package com.jikokujo.schedule.presentation.map

import android.graphics.Bitmap
import org.mapsforge.core.graphics.Bitmap as MapsforgeBitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.createBitmap
import org.mapsforge.map.android.graphics.AndroidGraphicFactory

class ShapeBuilderFactory private constructor(
    private val pixelDensity: Float,
    width: Int,
    height: Int,
) {
    private val widthPx = (width * pixelDensity).toInt()
    private val heightPx = (height * pixelDensity).toInt()
    private var bitmap: Bitmap = createBitmap(widthPx, heightPx)
    private val canvas: Canvas = Canvas(bitmap)

    fun addVerticalLine(x: Float = 0.5f, startY: Float = 0f, endY: Float = 1f, width: Float, color: Int) = apply{
        val paint = Paint()
        paint.strokeWidth = width
        paint.color = color

        this.canvas.drawLine(
            this.widthPx * x,
            this.heightPx * startY,
            this.widthPx * x,
            this.heightPx * endY,
            paint
        )
    }
    fun addCircle(radius: Float, x: Float = 0.5f, y: Float = 0.5f, color: Int) = apply{
        val paint = Paint()
        paint.color = color
        paint.strokeWidth = 10f

        this.canvas.drawCircle(
            this.widthPx * x,
            this.heightPx * y,
            radius * pixelDensity,
            paint
        )
    }
    fun buildToBitmap(): Bitmap = this.bitmap
    fun buildToMapsforgeBitmap(): MapsforgeBitmap = AndroidGraphicFactory.convertToBitmap(
        BitmapDrawable(this.bitmap)
    )

    companion object {
        fun size(width: Int, height: Int, pixelDensity: Float) = ShapeBuilderFactory(
            width = width,
            height = height,
            pixelDensity = pixelDensity
        )
    }
}