package com.jikokujo.map.presentation

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.jikokujo.R
import com.jikokujo.core.utils.darken
import com.jikokujo.core.utils.lighten
import com.jikokujo.core.utils.rawFile
import com.jikokujo.map.utils.MapUtils
import com.jikokujo.map.utils.ShapeBuilderFactory
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
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.cache.TileCache
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.overlay.Polyline
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes

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
    fun drawMapIfNotExist(localContext: Context) {
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
                tileRendererLayer.setXmlRenderTheme(MapsforgeThemes.DEFAULT)
                mapView.layerManager.layers.add(tileRendererLayer)

                val boundingBox = mapData.mapFileInfo.boundingBox
                mapView.model.mapViewPosition.mapLimit = boundingBox
                mapView.model.mapViewPosition.center = LatLong(47.4933, 19.0533)
                mapView.model.mapViewPosition.zoomLevel = 15
                mapView.model.mapViewPosition.zoomLevelMin = 8
            } catch(e: Exception) {
                Log.e("MAPSFORGE_MAP", "Mapsforge map failed to load: ${e.message}")
            }
        }
    }
    fun handleTrip(
        tripInfoState: TripInfoState,
        pixelDensity: Float
    ) = coroutineScope.launch {
        val onlyMapVisible: Boolean = mapView.layerManager.layers.count() <= 1
        val tripAvailable: Boolean = tripInfoState.pathPoints.isNotEmpty() && tripInfoState.stops.isNotEmpty()
        if (onlyMapVisible) {
            Log.i("INFO", "ONLY MAP VISIBLE")
            if (tripAvailable) {
                Log.i("INFO", "TRIP AVAILABLE FOR ONLY MAP VISIBLE")
                shownShapeId = tripInfoState.trip?.shapeId
                val tripPolyline = async {
                    return@async produceTripPolyline(
                        pathPoints = tripInfoState.pathPoints,
                        stops = tripInfoState.stops,
                        routeAssociated = tripInfoState.routeAssociated,
                        selectedThrough = tripInfoState.selectedThrough,
                    )
                }
                val tripStops = async {
                    return@async produceTripStops(
                        stops = tripInfoState.stops,
                        routeAssociated = tripInfoState.routeAssociated,
                        selectedThrough = tripInfoState.selectedThrough,
                        pixelDensity = pixelDensity
                    )
                }
                tripPolyline.await().forEach {
                    mapView.layerManager.layers.add(it)
                }
                tripStops.await().forEach {
                    mapView.layerManager.layers.add(it)
                }

                centerMapToTrip(tripInfoState.pathPoints)
                fitMapToTrip(tripInfoState.pathPoints)
            }
        } else {
            Log.i("INFO", "NOT ONLY MAP VISIBLE")
            if (tripAvailable) {
                Log.i("INFO", "TRIP AVAILABLE FOR NOT ONLY MAP VISIBLE")
                val tripIdChanged: Boolean = shownShapeId != tripInfoState.trip?.shapeId
                if (tripIdChanged) {
                    shownShapeId = tripInfoState.trip?.shapeId
                    clearLayersAboveMap()
                    val tripPolyline = async {
                        return@async produceTripPolyline(
                            pathPoints = tripInfoState.pathPoints,
                            stops = tripInfoState.stops,
                            routeAssociated = tripInfoState.routeAssociated,
                            selectedThrough = tripInfoState.selectedThrough,
                        )
                    }
                    val tripStops = async {
                        return@async produceTripStops(
                            stops = tripInfoState.stops,
                            routeAssociated = tripInfoState.routeAssociated,
                            selectedThrough = tripInfoState.selectedThrough,
                            pixelDensity = pixelDensity
                        )
                    }
                    tripPolyline.await().forEach {
                        mapView.layerManager.layers.add(it)
                    }
                    tripStops.await().forEach {
                        mapView.layerManager.layers.add(it)
                    }

                    centerMapToTrip(tripInfoState.pathPoints)
                    fitMapToTrip(pathPoints = tripInfoState.pathPoints)
                }
            } else {
                shownShapeId = null
                clearLayersAboveMap()
            }
        }
    }
    fun centerMapToTrip(pathPoints: List<RoutePathPoint>){
        val centerPoint = MapUtils.calculateCenterPoint(pathPoints)

        mapView.setCenter(
            LatLong(
                centerPoint.lat,
                centerPoint.lon,
            )
        )
    }
    fun fitMapToTrip(pathPoints: List<RoutePathPoint>){
        mapView.model.frameBufferModel.addObserver {
            val (minLocation, maxLocation) = MapUtils.findPathBoundaries(pathPoints)

            var isContained =
                mapView.boundingBox.contains(minLocation.lat, minLocation.lon) &&
                mapView.boundingBox.contains(maxLocation.lat, maxLocation.lon)


            while (!isContained) {
                mapView.model.mapViewPosition.zoomOut(true)
                isContained =
                    mapView.boundingBox.contains(minLocation.lat, minLocation.lon) &&
                    mapView.boundingBox.contains(maxLocation.lat, maxLocation.lon)
            }
        }
    }
    fun produceTripStops(
        stops: List<StopWithLocationAndStopTime>,
        routeAssociated: Queryable.Route?,
        selectedThrough: Queryable?,
        pixelDensity: Float
    ): List<Marker> {
        val markerSize = 19f
        val markerBitmapAfterStop = ShapeBuilderFactory
            .size(38, 38, pixelDensity)
            .addCircle(
                radius = markerSize,
                color = routeAssociated?.getColor()?.darken(0.15f)?.toArgb() ?: Color.DarkGray.lighten(0.15f).toArgb()
            )
            .addCircle(
                radius = markerSize - 5f,
                color = routeAssociated?.getColor()?.lighten(0.15f)?.toArgb() ?: Color.DarkGray.darken(0.15f).toArgb()
            )
            .buildToMapsforgeBitmap()
        val markers: MutableList<Marker> = mutableListOf()
        val wasStopSelected = selectedThrough as? Queryable.Stop != null
        if (wasStopSelected) {
            val markerBitmapBeforeStop = ShapeBuilderFactory
                .size(38, 38, pixelDensity)
                .addCircle(
                    radius = markerSize,
                    color = Color.DarkGray.darken(0.15f).toArgb()
                )
                .addCircle(
                    radius = markerSize - 5f,
                    color = Color.DarkGray.lighten(0.15f).toArgb()
                )
                .buildToMapsforgeBitmap()
            var selectedStopSeen = false
            stops.forEach { stop ->
                if (selectedThrough.ids.contains(stop.id) && !selectedStopSeen){
                    selectedStopSeen = true
                }
                markers.add(
                    Marker(
                        LatLong(stop.location.lat, stop.location.lon),
                        if (selectedStopSeen) markerBitmapAfterStop else markerBitmapBeforeStop,
                        0,
                        0
                    )
                )
            }
        } else {
            stops.forEach { stop ->
                markers.add(
                    Marker(
                        LatLong(stop.location.lat, stop.location.lon),
                        markerBitmapAfterStop,
                        0,
                        0
                    )
                )
            }
        }
        return markers
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
                stopIds = selectedThrough.ids,
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
        stopIds: List<String>,
        paintAfterStop: Paint,
        stroke: Float
    ): List<Polyline>{
        val paintBeforeStop: Paint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
            color = Color.DarkGray.toArgb()
            strokeWidth = stroke
            setStyle(Style.STROKE)
            setStrokeCap(Cap.ROUND)
            setStrokeJoin(Join.ROUND)
        }
        val routeBeforeStop: MutableList<LatLong> = mutableListOf()
        val routeAfterStop: MutableList<LatLong> = mutableListOf()
        val switchingPoint = MapUtils.findClosestLocationIndex(
            points = pathPoints,
            target = stops.find {
                stopIds.contains(it.id)
            }!!.location
        )
        for (i in 0..<pathPoints.count()){
            if (i < switchingPoint){
                routeBeforeStop.add(
                    LatLong(
                        pathPoints[i].location.lat,
                        pathPoints[i].location.lon
                    )
                )
            } else {
                routeAfterStop.add(
                    LatLong(
                        pathPoints[i].location.lat,
                        pathPoints[i].location.lon
                    )
                )
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