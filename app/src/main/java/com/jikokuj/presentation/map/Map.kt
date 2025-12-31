package com.jikokuj.presentation.map

import android.content.Context
import androidx.annotation.RawRes
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokuj.R
import com.jikokuj.domain.model.Location
import org.mapsforge.core.model.Rotation
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.rendertheme.AssetsRenderTheme
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.cache.TileCache
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes
import java.io.File
import java.io.FileOutputStream

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
    val mapViewModel = viewModel<MapViewModel>()
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit){
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    mapViewModel.onAction(Action.Move(Location.Anonymous(
                        lon = mapViewModel.state.value.center.longitude - dragAmount.x / 100_000,
                        lat = mapViewModel.state.value.center.latitude + dragAmount.y / 100_000
                    )))
                }
            }
    ) {
        MapsforgeMap(modifier, mapViewModel.state.collectAsStateWithLifecycle().value)
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