package com.jikokujo.schedule.presentation.map

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.RoutePathPoint
import org.mapsforge.core.graphics.Bitmap
import org.mapsforge.core.graphics.Style
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import kotlin.math.pow
import kotlin.math.sqrt

object MapUtils {
    fun createSimpleDotBitmap(radius: Float, color: Color): Bitmap {
        val padding = 4
        val primaryColor = AndroidGraphicFactory.INSTANCE.createPaint().apply {
            this.color = color.copy(
                red = color.red - 0.2f,
                green = color.green - 0.2f,
                blue = color.blue - 0.2f
            ).toArgb()
            setStyle(Style.FILL)
        }
        val secondaryColor = AndroidGraphicFactory.INSTANCE.createPaint().apply {
            this.color = color.copy(
                red = color.red + 0.2f,
                green = color.green + 0.2f,
                blue = color.blue + 0.2f
            ).toArgb()
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
                primaryColor
            )
            drawCircle(
                radius.toInt() + padding / 2,
                radius.toInt() + padding / 2,
                (radius / 1.5).toInt(),
                secondaryColor
            )
        }
        return bitmap
    }
    fun findClosestLocationIndex(list: List<RoutePathPoint>, point: Location.Stop): Int{
        var closestI = 0
        for (i in 1..<list.count()){
            val currentDist = sqrt((point.lon - list[i].location.lon).pow(2) - (point.lat - list[i].location.lat).pow(2))
            val closestDist = sqrt((point.lon - list[closestI].location.lon).pow(2) - (point.lat - list[closestI].location.lat).pow(2))
            if (closestDist > currentDist) {
                closestI = i
            }
        }
        return closestI
    }
}