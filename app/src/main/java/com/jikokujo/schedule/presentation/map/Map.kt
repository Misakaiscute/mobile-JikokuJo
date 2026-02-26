package com.jikokujo.schedule.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.view.MapView

@Composable
fun Map(
    modifier: Modifier,
    state: TripInfoState,
    onAction: (TripAction) -> Unit
){
    MapsforgeMap(
        modifier = modifier,
        state = state,
    )
    TripInfoSheet(
        modifier = Modifier,
        state = state,
        onAction = { action ->
            onAction(action)
        }
    )
}
@Composable
fun MapsforgeMap(
    modifier: Modifier,
    state: TripInfoState,
){
    val localContext = LocalContext.current
    val pixelDensity = LocalDensity.current.density
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
                initialZoom = 15
            )
            mapActionHandler.handleTrip(
                tripInfoState = state,
                pixelDensity = pixelDensity
            )
        }
    )
}