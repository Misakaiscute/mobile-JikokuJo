package com.jikokujo.map.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.jikokujo.core.utils.EmulatorDetector
import com.jikokujo.map.presentation.tripinfosheet.TripInfoSheet
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
    if (state.loading.contains(Loadable.Trip())){
        LoadingIndicator(modifier = Modifier)
    }
}
@Composable
private fun MapsforgeMap(
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
                setBuiltInZoomControls(EmulatorDetector.isEmulator)
            }
            mapActionHandler.bindMapView(mapView)
            mapView
        },
        update = {
            mapActionHandler.drawMapIfNotExist(
                localContext = localContext,
            )
            mapActionHandler.handleTrip(
                tripInfoState = state,
                pixelDensity = pixelDensity
            )
        }
    )
}
@Composable
private fun LoadingIndicator(modifier: Modifier){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(1/2f)
                    .aspectRatio(1f),
                color = Color.White,
                strokeCap = StrokeCap.Round
            )
        }
    }
}