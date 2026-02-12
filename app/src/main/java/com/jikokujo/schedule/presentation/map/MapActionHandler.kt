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
    fun handleTrip(
        shapeId: String?,
        pathPoints: List<RoutePathPoint>,
        stops: List<StopWithLocationAndStopTime>,
        routeAssociated: Queryable.Route?
    ) = coroutineScope.launch {
        val onlyMapVisible: Boolean = mapView.layerManager.layers.count() <= 1
        val tripAvailable: Boolean = pathPoints.isNotEmpty() && stops.isNotEmpty()
        if (onlyMapVisible) {
            if (tripAvailable) {
                shownShapeId = shapeId
                launch {
                    addTripPolyline(
                        pathPoints = pathPoints,
                        routeAssociated = routeAssociated
                    )
                }
                launch {
                    addTripStops(
                        stops = stops,
                        routeAssociated = routeAssociated
                    )
                }
            }
        } else {
            if (tripAvailable) {
                val tripIdChanged: Boolean = shownShapeId != shapeId
                if (tripIdChanged) {
                    shownShapeId = shapeId
                    clearLayersAboveMap()
                    launch {
                        addTripPolyline(
                            pathPoints = pathPoints,
                            routeAssociated = routeAssociated
                        )
                    }
                    launch {
                        addTripStops(
                            stops = stops,
                            routeAssociated = routeAssociated
                        )
                    }
                }
            } else {
                shownShapeId = null
                clearLayersAboveMap()
            }
        }
    }
    private fun addTripPolyline(pathPoints: List<RoutePathPoint>, routeAssociated: Queryable.Route?) {
        val paint: Paint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
            color = routeAssociated!!.getColor().toArgb()
            strokeWidth = 15f
            setStyle(Style.STROKE)
            setStrokeCap(Cap.ROUND)
            setStrokeJoin(Join.ROUND)
        }
        val route: List<LatLong> = pathPoints.map { pathPoint ->
            LatLong(pathPoint.location.lat, pathPoint.location.lon)
        }.toList()
        val routePolyline = Polyline(
            paint,
            AndroidGraphicFactory.INSTANCE
        )
        routePolyline.setPoints(route)
        mapView.layerManager.layers.add(routePolyline)
    }
    fun addTripStops(stops: List<StopWithLocationAndStopTime>, routeAssociated: Queryable.Route?) {
        val markerBitmap = MapUtils.createSimpleDotBitmap(
            radius = 20f,
            color = routeAssociated?.getColor() ?: Color.Black
        )
        val markers: List<Marker> = stops.map { stop ->
            Marker(
                LatLong(stop.location.lat, stop.location.lon),
                markerBitmap,
                0,
                0
            )
        }
        markers.forEach { marker ->
            mapView.layerManager.layers.add(marker)
        }
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