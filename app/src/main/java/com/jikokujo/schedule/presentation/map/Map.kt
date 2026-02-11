package com.jikokujo.schedule.presentation.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jikokujo.R
import com.jikokujo.core.utils.rawFile
import com.jikokujo.schedule.data.model.getColor
import com.jikokujo.theme.Typography
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
import org.mapsforge.map.layer.overlay.Polyline
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import kotlin.collections.map

@Composable
fun DisplayMapsforgeMap(
    modifier: Modifier,
    state: MapState,
    layerState: MapLayerState,
    onAction: (Action) -> Unit
){
    MapsforgeMap(
        modifier = modifier,
        state = state,
        layerState = layerState
    )
    Box(
        modifier = modifier
            .background(Color.Transparent)
            .padding(bottom = 35.dp, top = 0.dp, start = 6.dp, end = 6.dp)
            .fillMaxSize()
    ){
        Column(
            modifier.fillMaxWidth(1/7f)
        ) {
            Spacer(modifier = modifier.weight(1f))
            Button(
                modifier = modifier,
                colors = ButtonDefaults.buttonColors().copy(
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(5.dp),
                onClick = { onAction(Action.ChangeZoomLevel(true)) }
            ){
                Text(
                    text = "+",
                    style = Typography.labelLarge.merge(
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Button(
                modifier = modifier,
                colors = ButtonDefaults.buttonColors().copy(
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(5.dp),
                onClick = { onAction(Action.ChangeZoomLevel(false)) }
            ){
                Text(
                    text = "-",
                    style = Typography.labelLarge.merge(
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
@Composable
fun MapsforgeMap(
    modifier: Modifier,
    state: MapState,
    layerState: MapLayerState
){
    val localContext = LocalContext.current
    val scope = rememberCoroutineScope()

    val mapData = MapFile(localContext.rawFile(R.raw.budapest))

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            AndroidGraphicFactory.createInstance(context.applicationContext)
            MapView(context).apply {
                isClickable = true
                mapScaleBar.isVisible = true
                setBuiltInZoomControls(false)
            }
        },
        update = { mapView ->
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
                    mapView.model.mapViewPosition.zoomLevel = state.zoomLevel
                } catch(e: Exception) {
                    Log.e("MAPSFORGE_MAP", "Mapsforge map failed to load: ${e.message}")
                }
            }
            scope.launch {
                //Handle zooming in and out
                if (mapView.model.mapViewPosition.zoomLevel < state.zoomLevel){
                    mapView.model.mapViewPosition.zoomIn(true)
                } else if (mapView.model.mapViewPosition.zoomLevel > state.zoomLevel){
                    mapView.model.mapViewPosition.zoomOut(true)
                }
                mapView.model.mapViewPosition.rotation = Rotation(state.rotation, 0f, 0f)
            }
            scope.launch {
                //Placing & clearing line of a trip
                if (!layerState.pathPoints.isEmpty() && !layerState.stops.isEmpty()) {
                    if (mapView.layerManager.layers.count() > 1){
                        var index = mapView.layerManager.layers.count() - 1
                        while (index > 0) {
                            mapView.layerManager.layers.remove(index, true)
                            index--
                        }
                    }
                    val paint: Paint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
                        color = layerState.routeAssociated!!.getColor().toArgb()
                        strokeWidth = 20f
                        setStyle(Style.STROKE)
                        setStrokeCap(Cap.ROUND)
                        setStrokeJoin(Join.ROUND)
                    }
                    val route: List<LatLong> = layerState.pathPoints.map { pathPoint ->
                        LatLong(pathPoint.location.lat, pathPoint.location.lon)
                    }.toList()
                    val routePolyline = Polyline(
                        paint,
                        AndroidGraphicFactory.INSTANCE
                    )
                    routePolyline.setPoints(route)
                    mapView.layerManager.layers.add(routePolyline)
                } else {
                    if (mapView.layerManager.layers.count() > 1){
                        var index = mapView.layerManager.layers.count() - 1
                        while (index > 0) {
                            mapView.layerManager.layers.remove(index)
                            index--
                        }
                    }
                }
            }
//            if (layerState.pathPoints.count() > 1 && layerState.stops.count() > 1){
//                if (mapView.layerManager.layers.count() < 2){
//                    val paint: Paint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
//                        color = layerState.routeAssociated!!.getColor().toArgb()
//                        strokeWidth = 20f
//                        setStyle(Style.STROKE)
//                        setStrokeCap(Cap.ROUND)
//                        setStrokeJoin(Join.ROUND)
//                    }
//                    val route: List<LatLong> = layerState.pathPoints.map { pathPoint ->
//                        LatLong(pathPoint.location.lat, pathPoint.location.lon)
//                    }.toList()
//                    val routePolyline = Polyline(
//                        paint,
//                        AndroidGraphicFactory.INSTANCE
//                    )
//                    routePolyline.setPoints(route)
//                    mapView.layerManager.layers.add(routePolyline)
//                }
//            } else {
//                if (mapView.layerManager.layers.count() > 1){
//                    mapView.layerManager.layers.remove(
//                        mapView.layerManager.layers.count() - 1,
//                        true
//                    )
//                }
//            }
        }
    )
}