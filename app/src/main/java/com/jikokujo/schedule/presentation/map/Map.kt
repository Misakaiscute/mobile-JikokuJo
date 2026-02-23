package com.jikokujo.schedule.presentation.map

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.jikokujo.theme.Typography
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.view.MapView

@Composable
fun DisplayMapsforgeMap(
    modifier: Modifier,
    state: MapState,
    layerState: MapLayerState,
    onAction: (MapAction) -> Unit
){
    MapsforgeMap(
        modifier = modifier,
        state = state,
        layerState = layerState,
        onAction = { action -> onAction(action) }
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
                onClick = { onAction(MapAction.SetZoomLevel(state.zoomLevel + 1)) }
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
                onClick = { onAction(MapAction.SetZoomLevel(state.zoomLevel - 1)) }
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
    layerState: MapLayerState,
    onAction: (MapAction) -> Unit
){
    val localContext = LocalContext.current
    val mapActionHandler = remember { MapActionHandler() }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            AndroidGraphicFactory.createInstance(context.applicationContext)
            val mapView = MapView(context).apply {
                isClickable = true
                mapScaleBar.isVisible = true
                setBuiltInZoomControls(false)
            }
            mapActionHandler.bindMapView(mapView)
            mapView
        },
        update = {
            mapActionHandler.drawMapIfDoesntExist(
                localContext = localContext,
                initialZoom = state.zoomLevel
            )

            mapActionHandler.handleZoom(state.zoomLevel)
            mapActionHandler.handleRotation(state.rotation)

            mapActionHandler.handleTrip(
                layerState = layerState,
                stateAction = onAction
            )
        }
    )
}