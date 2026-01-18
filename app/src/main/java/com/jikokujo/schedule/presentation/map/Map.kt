package com.jikokujo.schedule.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jikokujo.R
import com.jikokujo.core.utils.rawFile
import com.jikokujo.schedule.data.model.getColor
import com.jikokujo.theme.Typography
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

@Composable
fun DisplayMapsforgeMap(
    modifier: Modifier,
    state: MapState,
    onAction: (Action) -> Unit
){
    MapsforgeMap(
        modifier = modifier,
        state = state
    )
    Box(
        modifier = modifier
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
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp),
                onClick = { onAction(Action.ChangeZoomLevel(true)) }
            ){
                Text(
                    text = "+",
                    style = Typography.labelLarge.merge(
                        TextStyle(textAlign = TextAlign.Center)
                    )
                )
            }
            Button(
                modifier = modifier,
                colors = ButtonDefaults.buttonColors().copy(
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(5.dp),
                onClick = { onAction(Action.ChangeZoomLevel(false)) }
            ){
                Text(
                    text = "-",
                    style = Typography.labelLarge.merge(
                        TextStyle(textAlign = TextAlign.Center)
                    )
                )
            }
        }
    }
}
@Composable
fun MapsforgeMap(
    modifier: Modifier,
    state: MapState
){
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AndroidGraphicFactory.createInstance(context.applicationContext)
            MapView(context).apply {
                isClickable = true
                mapScaleBar.isVisible = true
                setBuiltInZoomControls(false)

                val tileCache: TileCache = AndroidUtil.createExternalStorageTileCache(
                    context,
                    "mapcache",
                    256,
                    24,
                    false
                )
                val mapData = MapFile(context.rawFile(R.raw.budapest))
                val tileRendererLayer = TileRendererLayer(
                    tileCache,
                    mapData,
                    model.mapViewPosition,
                    AndroidGraphicFactory.INSTANCE
                )
                layerManager.layers.add(tileRendererLayer)
                val mapThemeFile = AssetsRenderTheme(context.assets, "", "map_theme.xml")
                tileRendererLayer.setXmlRenderTheme(mapThemeFile)

                val boundingBox = mapData.mapFileInfo.boundingBox
                model.mapViewPosition.mapLimit = boundingBox
                model.mapViewPosition.center = LatLong(47.4933, 19.0533)
            }
        },
        update = { mapView ->
            //Handle zooming in and out
            if (mapView.model.mapViewPosition.zoomLevel < state.zoomLevel){
                mapView.model.mapViewPosition.zoomIn(true)
            } else if (mapView.model.mapViewPosition.zoomLevel > state.zoomLevel){
                mapView.model.mapViewPosition.zoomOut(true)
            }

            //Placing & clearing line of a trip
            if (state.pathPoints.count() > 1) {
                val paint: Paint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
                    color = state.routeAssociated!!.getColor().toArgb()
                    strokeWidth = 8f
                    setStyle(Style.STROKE)
                    setStrokeCap(Cap.ROUND)
                    setStrokeJoin(Join.ROUND)
                }
                val route: List<LatLong> = state.pathPoints.map { pathPoint ->
                    LatLong(pathPoint.lat, pathPoint.lon)
                }.toList()
                val polyline: Polyline = Polyline(
                    paint,
                    AndroidGraphicFactory.INSTANCE
                )

                polyline.setPoints(route)
                mapView.layerManager.layers.add(polyline)
            } else {
                mapView.layerManager.layers.clear()
            }

            mapView.model.mapViewPosition.apply {
                rotation = Rotation(state.rotation, 0f, 0f)
            }
        }
    )
}