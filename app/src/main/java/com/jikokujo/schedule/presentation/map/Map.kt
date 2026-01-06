package com.jikokujo.schedule.presentation.map

import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokujo.R
import com.jikokujo.theme.Typography
import com.jikokujo.schedule.data.model.Location
import org.mapsforge.core.model.Rotation
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.rendertheme.AssetsRenderTheme
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.cache.TileCache
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import java.io.File
import java.io.FileOutputStream
import kotlin.math.pow

fun Context.rawFile(@RawRes resId: Int): File =
    File(cacheDir, "raw_${resources.getResourceEntryName(resId)}").apply {
        if(!exists()) {
            resources.openRawResource(resId).use { input ->
                FileOutputStream(this).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

@Composable
fun DisplayMapsforgeMap(modifier: Modifier){
    val focusManager = LocalFocusManager.current
    val mapViewModel = viewModel<MapViewModel>()
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionSource = MutableInteractionSource()
            )
            .pointerInput(Unit){
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val changeX = dragAmount.x / (500 / mapViewModel.state.value.zoomLevel.toDouble().pow(-2.2))
                    val changeY = dragAmount.y / (500 / mapViewModel.state.value.zoomLevel.toDouble().pow(-2.2))
                    mapViewModel.onAction(Action.Move(Location.Anonymous(
                        lon = mapViewModel.state.value.center.longitude - changeX,
                        lat = mapViewModel.state.value.center.latitude + changeY
                    )))
                }
            }
    ) {
        MapsforgeMap(modifier, mapViewModel.state.collectAsStateWithLifecycle().value)
    }
    Box(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ){
        Column(
            modifier.fillMaxWidth(1/7f)
        ) {
            Spacer(modifier = modifier.weight(1f))
            Button(
                modifier = modifier,
                shape = RoundedCornerShape(5.dp),
                onClick = {
                    mapViewModel.onAction(Action.ChangeZoomLevel(
                        zoomLevel = (mapViewModel.state.value.zoomLevel + 1).toByte()
                    ))
                }
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
                shape = RoundedCornerShape(5.dp),
                onClick = { mapViewModel.onAction(Action.ChangeZoomLevel(
                    zoomLevel = (mapViewModel.state.value.zoomLevel - 1).toByte()
                )) }
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
                isClickable = false
                mapScaleBar.isVisible = false
                setBuiltInZoomControls(true)

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

                model.mapViewPosition.apply {
                    center = state.center
                    zoomLevel = state.zoomLevel
                    rotation = Rotation(state.rotation, 0f, 0f)
                }
            }
        },
        update = { mapView ->
            mapView.model.mapViewPosition.apply {
                center = state.center
                zoomLevel = state.zoomLevel
                rotation = Rotation(state.rotation, 0f, 0f)
            }
        }
    )
}