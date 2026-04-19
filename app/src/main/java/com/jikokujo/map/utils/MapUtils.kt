package com.jikokujo.map.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.RoutePathPoint
import org.mapsforge.core.graphics.Bitmap
import org.mapsforge.core.graphics.Style
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import kotlin.math.abs
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
    fun findClosestLocationIndex(points: List<RoutePathPoint>, target: Location.Stop): Int{
        var closestI = 0
        for (i in 1..<points.count()){
            val currentDist = sqrt(abs(
                (target.lon - points[i].location.lon).pow(2) - (target.lat - points[i].location.lat).pow(2)
            ))
            val closestDist = sqrt(abs(
                (target.lon - points[closestI].location.lon).pow(2) - (target.lat - points[closestI].location.lat).pow(2)
            ))
            if (closestDist > currentDist) {
                closestI = i
            }
        }
        return closestI
    }
    @Throws(ArithmeticException::class)
    fun calculateCenterPoint(points: List<RoutePathPoint>): Location.Auxiliary{
        if (points.isEmpty()){
            throw ArithmeticException("Points must contain at least one item at least.")
        } else {
            val centerX: Double = points.sumOf {
                it.location.lat
            } / points.count()
            val centerY: Double = points.sumOf {
                it.location.lon
            } / points.count()

            return Location.Auxiliary(
                lat = centerX,
                lon = centerY
            )
        }
    }
    fun findPathBoundaries(points: List<RoutePathPoint>): Pair<Location.Auxiliary, Location.Auxiliary>{
        if (points.isEmpty()){
            return Location.Auxiliary(0.0, 0.0) to Location.Auxiliary(0.0, 0.0)
        } else {
            var minLat = points[0].location.lat
            var maxLat = points[0].location.lon
            var minLon = points[0].location.lat
            var maxLon = points[0].location.lon
            for (i in 1..<points.count()){
                if (points[i].location.lat < minLat){
                    minLat = points[i].location.lat
                } else if (points[i].location.lat > maxLat){
                    maxLat = points[i].location.lat
                }

                if (points[i].location.lon < minLon){
                    minLon = points[i].location.lon
                } else if (points[i].location.lon > maxLon){
                    maxLon = points[i].location.lon
                }
            }
            return Location.Auxiliary(minLon, minLat) to Location.Auxiliary(maxLon, maxLat)
        }
    }
}