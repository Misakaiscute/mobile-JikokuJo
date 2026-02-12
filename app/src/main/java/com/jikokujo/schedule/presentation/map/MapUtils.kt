package com.jikokujo.schedule.presentation.map

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.mapsforge.core.graphics.Bitmap
import org.mapsforge.core.graphics.Style
import org.mapsforge.map.android.graphics.AndroidGraphicFactory

object MapUtils {
    fun createSimpleDotBitmap(radius: Float, color: Color): Bitmap {
        val padding = 4
        val paint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
            this.color = color.toArgb()
            setStyle(Style.FILL)
        }
        val bitmap = AndroidGraphicFactory.INSTANCE.createBitmap(
            (radius * 2).toInt() + padding,
            (radius * 2).toInt() + padding
        )
        AndroidGraphicFactory.INSTANCE.createCanvas().apply {
            setBitmap(bitmap)
            drawCircle(
                radius.toInt() + padding / 2,
                radius.toInt() + padding / 2,
                radius.toInt(),
                paint
            )
        }
        return bitmap
    }

}