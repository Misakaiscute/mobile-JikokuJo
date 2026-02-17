package com.jikokujo.schedule.presentation.map

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jikokujo.R
import com.jikokujo.core.utils.rawFile
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.RoutePathPoint
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.getColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.mapsforge.core.graphics.Cap
import org.mapsforge.core.graphics.Join
import org.mapsforge.core.graphics.Paint
import org.mapsforge.core.graphics.Style
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Rotation
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.rendertheme.AssetsRenderTheme
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.cache.TileCache
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.overlay.Polyline
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile

class MapActionHandler {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private lateinit var mapView: MapView
    private var shownShapeId: String? = null

    fun bindMapView(mapView: MapView) {
        if (this::mapView.isInitialized){
            throw Exception("MapView is already bound")
        }
        this.mapView = mapView
    }

    fun drawMapIfDoesntExist(localContext: Context, initialZoom: Byte) {
        val mapData = MapFile(localContext.rawFile(R.raw.budapest))

        if (mapView.layerManager.layers.isEmpty){
            try {
                val tileCache: TileCache = AndroidUtil.createExternalStorageTileCache(
                    localContext,
                    "mapcache",
                    64,
                    mapView.model.displayModel.tileSize,
                    false
                )
                val tileRendererLayer = TileRendererLayer(
                    tileCache,
                    mapData,
                    mapView.model.mapViewPosition,
                    AndroidGraphicFactory.INSTANCE
                )
                val mapThemeFile = AssetsRenderTheme(localContext.assets, "", "map_theme.xml")
                tileRendererLayer.setXmlRenderTheme(mapThemeFile)
                mapView.layerManager.layers.add(tileRendererLayer)

                val boundingBox = mapData.mapFileInfo.boundingBox
                mapView.model.mapViewPosition.mapLimit = boundingBox
                mapView.model.mapViewPosition.center = LatLong(47.4933, 19.0533)
                mapView.model.mapViewPosition.zoomLevel = initialZoom
            } catch(e: Exception) {
                Log.e("MAPSFORGE_MAP", "Mapsforge map failed to load: ${e.message}")
            }
        }
    }
    fun handleZoom(toZoomLevel: Byte) = coroutineScope.launch {
        if (mapView.model.mapViewPosition.zoomLevel < toZoomLevel){
            mapView.model.mapViewPosition.zoomIn(true)
        } else if (mapView.model.mapViewPosition.zoomLevel > toZoomLevel){
            mapView.model.mapViewPosition.zoomOut(true)
        }
    }
    fun handleRotation(toRotationDeg: Float) = coroutineScope.launch {
        mapView.model.mapViewPosition.rotation = Rotation(
            toRotationDeg,
            0f,
            0f
        )
    }
    fun handleTrip(layerState: MapLayerState) = coroutineScope.launch {
        val onlyMapVisible: Boolean = mapView.layerManager.layers.count() <= 1
        val tripAvailable: Boolean = layerState.pathPoints.isNotEmpty() && layerState.stops.isNotEmpty()
        if (onlyMapVisible) {
            if (tripAvailable) {
                shownShapeId = layerState.shapeId
                val tripPolyline = async {
                    return@async produceTripPolyline(
                        pathPoints = layerState.pathPoints,
                        stops = layerState.stops,
                        routeAssociated = layerState.routeAssociated,
                        selectedThrough = layerState.selectedThrough,
                    )
                }
                val tripStops = async {
                    return@async produceTripStops(
                        stops = layerState.stops,
                        routeAssociated = layerState.routeAssociated,
                        selectedThrough = layerState.selectedThrough
                    )
                }
                tripPolyline.await().forEach {
                    mapView.layerManager.layers.add(it)
                }
                tripStops.await().forEach {
                    mapView.layerManager.layers.add(it)
                }
            }
        } else {
            if (tripAvailable) {
                val tripIdChanged: Boolean = shownShapeId != layerState.shapeId
                if (tripIdChanged) {
                    shownShapeId = layerState.shapeId
                    clearLayersAboveMap()
                    val tripPolyline = async {
                        return@async produceTripPolyline(
                            pathPoints = layerState.pathPoints,
                            stops = layerState.stops,
                            routeAssociated = layerState.routeAssociated,
                            selectedThrough = layerState.selectedThrough,
                        )
                    }
                    val tripStops = async {
                        return@async produceTripStops(
                            stops = layerState.stops,
                            routeAssociated = layerState.routeAssociated,
                            selectedThrough = layerState.selectedThrough
                        )
                    }
                    tripPolyline.await().forEach {
                        mapView.layerManager.layers.add(it)
                    }
                    tripStops.await().forEach {
                        mapView.layerManager.layers.add(it)
                    }
                }
            } else {
                shownShapeId = null
                clearLayersAboveMap()
            }
        }
    }
    private fun produceTripPolyline(
        pathPoints: List<RoutePathPoint>,
        stops: List<StopWithLocationAndStopTime>,
        routeAssociated: Queryable.Route?,
        selectedThrough: Queryable?
    ): List<Polyline> {
        val stroke = 15f
        val paint: Paint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
            color = routeAssociated!!.getColor().toArgb()
            strokeWidth = stroke
            setStyle(Style.STROKE)
            setStrokeCap(Cap.ROUND)
            setStrokeJoin(Join.ROUND)
        }
        val wasStopSelected = selectedThrough as? Queryable.Stop != null
        return if (wasStopSelected) {
            polylineThroughStop(
                pathPoints = pathPoints,
                stops = stops,
                stopId = selectedThrough.id,
                paintAfterStop = paint,
                stroke = stroke
            )
        } else {
            polylineThroughRoute(
                pathPoints = pathPoints,
                paint = paint
            )
        }
    }
    private fun polylineThroughRoute(
        pathPoints: List<RoutePathPoint>,
        paint: Paint
    ): List<Polyline>{
        val route: List<LatLong> = pathPoints.map { pathPoint ->
            LatLong(pathPoint.location.lat, pathPoint.location.lon)
        }.toList()
        val routePolyline = Polyline(
            paint,
            AndroidGraphicFactory.INSTANCE
        ).apply {
            setPoints(route)
        }
        return listOf(routePolyline)
    }
    private fun polylineThroughStop(
        pathPoints: List<RoutePathPoint>,
        stops: List<StopWithLocationAndStopTime>,
        stopId: String,
        paintAfterStop: Paint,
        stroke: Float
    ): List<Polyline>{
        val paintBeforeStop: Paint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
            color = Color.Black.toArgb()
            strokeWidth = stroke
            setStyle(Style.STROKE)
            setStrokeCap(Cap.ROUND)
            setStrokeJoin(Join.ROUND)
        }
        val routeBeforeStop: MutableList<LatLong> = mutableListOf()
        val routeAfterStop: MutableList<LatLong> = mutableListOf()
        val switchingPoint = MapUtils.findClosestLocationIndex(
            list = pathPoints,
            point = stops.find { it.id == stopId }!!.location
        )
        for (i in 0..<pathPoints.count()){
            if (i < switchingPoint){
                routeBeforeStop.add(LatLong(
                    pathPoints[i].location.lat,
                    pathPoints[i].location.lon
                ))
            } else {
                routeAfterStop.add(LatLong(
                    pathPoints[i].location.lat,
                    pathPoints[i].location.lon
                ))
            }
        }
        val routeAfterStopPolyline = Polyline(paintAfterStop, AndroidGraphicFactory.INSTANCE).apply{
            setPoints(routeAfterStop)
        }
        val routeBeforeStopPolyline = Polyline(paintBeforeStop, AndroidGraphicFactory.INSTANCE).apply {
            setPoints(routeBeforeStop)
        }

        return listOf(routeBeforeStopPolyline, routeAfterStopPolyline)
    }
    fun produceTripStops(
        stops: List<StopWithLocationAndStopTime>,
        routeAssociated: Queryable.Route?,
        selectedThrough: Queryable?
    ): List<Marker> {
        val markerSize = 20f
        val markerBitmapAfterStop = MapUtils.createSimpleDotBitmap(
            radius = markerSize,
            color = routeAssociated?.getColor() ?: Color.Black
        )
        val markers: MutableList<Marker> = mutableListOf()
        val wasStopSelected = selectedThrough as? Queryable.Stop != null
        if (wasStopSelected) {
            val markerBitmapBeforeStop = MapUtils.createSimpleDotBitmap(
                radius = markerSize,
                color = Color.Black
            )
            var selectedStopSeen = false
            stops.forEach { stop ->
                if (stop.id == selectedThrough.id && !selectedStopSeen){
                    selectedStopSeen = true
                }
                markers.add(Marker(
                    LatLong(stop.location.lat, stop.location.lon),
                    if (selectedStopSeen) markerBitmapAfterStop else markerBitmapBeforeStop,
                    0,
                    0
                ))
            }
        } else {
            stops.forEach { stop ->
                markers.add(Marker(
                    LatLong(stop.location.lat, stop.location.lon),
                    markerBitmapAfterStop,
                    0,
                    0
                ))
            }
        }
        return markers
    }

    private fun clearLayersAboveMap() {
        if (mapView.layerManager.layers.count() > 1){
            var index = mapView.layerManager.layers.count() - 1
            while (index > 0) {
                mapView.layerManager.layers.remove(index)
                index--
            }
        }
    }
}