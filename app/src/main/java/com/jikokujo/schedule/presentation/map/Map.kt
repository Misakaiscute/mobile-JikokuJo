package com.jikokujo.schedule.presentation.map

import android.content.Context
import android.view.GestureDetector
import androidx.annotation.RawRes
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import kotlin.div
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
            .padding(bottom = 30.dp, top = 0.dp, start = 6.dp, end = 6.dp)
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