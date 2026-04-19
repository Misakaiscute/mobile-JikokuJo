package com.jikokujo.map.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jikokujo.core.utils.darken
import com.jikokujo.core.utils.lighten
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.arrivalTimeFormatted
import com.jikokujo.schedule.data.model.getColor
import com.jikokujo.map.utils.ShapeBuilderFactory
import com.jikokujo.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripInfoSheet(
    modifier: Modifier,
    state: TripInfoState,
    onAction: (TripAction) -> Unit
){
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()
    val itemHeight = 50

    val isStopSelected: Boolean = state.selectedThrough is Queryable.Stop

    if (state.tripInfoShown) {
        ModalBottomSheet(
            onDismissRequest = {
                onAction(TripAction.HideTripInfo)
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            if (isStopSelected) {
                TripInfoWithStopSelected(
                    modifier = modifier,
                    scrollState = scrollState,
                    state = state,
                    selectedStopIds = state.selectedThrough.ids,
                    elementHeight = itemHeight
                )
            } else {
                TripInfoWithRouteSelected(
                    modifier = modifier,
                    scrollState = scrollState,
                    state = state,
                    elementHeight = itemHeight
                )
            }
        }
    }
}
@Composable
private fun TripInfoWithStopSelected(
    modifier: Modifier,
    elementHeight: Int,
    scrollState: ScrollState,
    state: TripInfoState,
    selectedStopIds: List<String>,
) {
    val pixelDensity = LocalDensity.current.density
    val selectedStopId: String = remember {
        state.stops.find { stop ->
            selectedStopIds.contains(stop.id)
        }?.id ?: ""
    }
    val switchingStopIndex: Int = remember {
        state.stops.indexOfFirst { stop ->
            selectedStopId == stop.id
        }
    }

    val circleSize = 12f
    val routeColor = state.routeAssociated?.getColor() ?: Color.Black

    val startingPointColor = if (switchingStopIndex == 0) routeColor else Color.DarkGray
    val startingPointBitmap = remember { ShapeBuilderFactory.Companion
        .size(elementHeight, elementHeight, pixelDensity)
        .addVerticalLine(startY = 0.5f, width = 12f, color = startingPointColor.toArgb())
        .addCircle(radius = circleSize, color = startingPointColor.darken(0.15f).toArgb())
        .addCircle(radius = circleSize - 5f, color = startingPointColor.lighten(0.15f).toArgb())
        .buildToBitmap() }
    val intermediateBeforePointBitmap = remember { ShapeBuilderFactory.Companion
        .size(elementHeight, elementHeight, pixelDensity)
        .addVerticalLine(width = 12f, color = Color.DarkGray.toArgb())
        .addCircle(radius = circleSize, color = Color.DarkGray.darken(0.15f).toArgb())
        .addCircle(radius = circleSize - 5f, color = Color.DarkGray.lighten(0.15f).toArgb())
        .buildToBitmap() }
    val intermediateAfterPointBitmap = remember { ShapeBuilderFactory.Companion
        .size(elementHeight, elementHeight, pixelDensity)
        .addVerticalLine(width = 12f, color = routeColor.toArgb())
        .addCircle(radius = circleSize, color = routeColor.darken(0.15f).toArgb())
        .addCircle(radius = circleSize - 5f, color = routeColor.lighten(0.15f).toArgb())
        .buildToBitmap() }
    val switchingPointBitmap = remember { ShapeBuilderFactory.Companion
        .size(elementHeight, elementHeight, pixelDensity)
        .addVerticalLine(endY = 0.5f, width = 12f, color = Color.DarkGray.toArgb())
        .addVerticalLine(startY = 0.5f, width = 12f, color = routeColor.toArgb())
        .addCircle(radius = circleSize, color = routeColor.darken(0.15f).toArgb())
        .addCircle(radius = circleSize - 5f, color = routeColor.lighten(0.15f).toArgb())
        .buildToBitmap() }
    val endingPointColor = if (switchingStopIndex == state.stops.count() - 1) Color.DarkGray else routeColor
    val endingPointBitmap = remember { ShapeBuilderFactory.Companion
        .size(elementHeight, elementHeight, pixelDensity)
        .addVerticalLine(endY = 0.5f, width = 12f, color = endingPointColor.toArgb())
        .addCircle(radius = circleSize, color = routeColor.darken(0.15f).toArgb())
        .addCircle(radius = circleSize - 5f, color = routeColor.lighten(0.15f).toArgb())
        .buildToBitmap() }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.stops.forEachIndexed { i, stop ->
            TripInfoSheetItem(
                modifier = modifier,
                stop = stop,
                bitmap = when(i) {
                    0 -> startingPointBitmap
                    switchingStopIndex -> switchingPointBitmap
                    state.stops.count() - 1 -> endingPointBitmap
                    in 1..<switchingStopIndex -> intermediateBeforePointBitmap
                    in (switchingStopIndex + 1)..<(state.stops.count() - 1) -> intermediateAfterPointBitmap
                    else -> intermediateAfterPointBitmap
                },
                height = elementHeight
            )
        }
    }
}
@Composable
private fun TripInfoWithRouteSelected(
    modifier: Modifier,
    elementHeight: Int,
    scrollState: ScrollState,
    state: TripInfoState,
) {
    val pixelDensity = LocalDensity.current.density
    val circleSize = 12f
    val routeColor = state.routeAssociated?.getColor() ?: Color.Black

    val startingPointBitmap = remember { ShapeBuilderFactory.Companion
        .size(elementHeight, elementHeight, pixelDensity)
        .addVerticalLine(startY = 0.5f, width = 12f, color = routeColor.toArgb())
        .addCircle(radius = circleSize, color = routeColor.darken(0.15f).toArgb())
        .addCircle(radius = circleSize - 5f, color = routeColor.lighten(0.15f).toArgb())
        .buildToBitmap() }
    val intermediatePointBitmap = remember { ShapeBuilderFactory.Companion
        .size(elementHeight, elementHeight, pixelDensity)
        .addVerticalLine(width = 12f, color = routeColor.toArgb())
        .addCircle(radius = circleSize, color = routeColor.darken(0.15f).toArgb())
        .addCircle(radius = circleSize - 5f, color = routeColor.lighten(0.15f).toArgb())
        .buildToBitmap() }
    val endingPointBitmap = remember { ShapeBuilderFactory.Companion
        .size(elementHeight, elementHeight, pixelDensity)
        .addVerticalLine(endY = 0.5f, width = 12f, color = routeColor.toArgb())
        .addCircle(radius = circleSize, color = routeColor.darken(0.15f).toArgb())
        .addCircle(radius = circleSize - 5f, color = routeColor.lighten(0.15f).toArgb())
        .buildToBitmap() }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.stops.forEachIndexed { i, stop ->
            TripInfoSheetItem(
                modifier = modifier,
                stop = stop,
                bitmap = when (i) {
                    0 -> startingPointBitmap
                    state.stops.count() - 1 -> endingPointBitmap
                    else -> intermediatePointBitmap
                },
                height = elementHeight
            )
        }
    }
}
@Composable
private fun TripInfoSheetItem(
    modifier: Modifier,
    stop: StopWithLocationAndStopTime,
    bitmap: Bitmap,
    height: Int
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null
        )
        Text(
            modifier = modifier.weight(100f),
            text = stop.name,
            style = Typography.bodyMedium.merge(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = modifier.weight(1f))
        VerticalDivider(
            modifier = modifier.padding(vertical = 3.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = modifier.padding(horizontal = 15.dp),
            text = stop.arrivalTimeFormatted(),
            style = Typography.bodyLarge.merge(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}