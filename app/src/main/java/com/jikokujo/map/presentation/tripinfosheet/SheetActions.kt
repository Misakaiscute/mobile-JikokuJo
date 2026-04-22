package com.jikokujo.map.presentation.tripinfosheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.map.presentation.TripAction
import com.jikokujo.map.presentation.TripInfoState

@Composable
fun SheetActions(
    modifier: Modifier,
    state: TripInfoState,
    onAction: (TripAction) -> Unit
) {
    Row(
        modifier = modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        ToggleFavouriteButton(
            modifier = Modifier,
            state = state,
            onAction = { action ->
                onAction(action)
            }
        )
    }
}
@Composable
private fun ToggleFavouriteButton(
    modifier: Modifier,
    state: TripInfoState,
    onAction: (TripAction) -> Unit
){
    val isTripFavourite: Boolean = state.favourites?.find {
        return@find it.route.id == state.trip?.routeId && it.atMins == state.trip.stops[0].arrivalTime
    } != null
    Button(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1f),
        onClick = {
            onAction(TripAction.ToggleFavourite(
                routeId = state.trip!!.routeId,
                atMins = state.trip.stops[0].arrivalTime
            ))
        }
    ){
        if (isTripFavourite) {
            Icon(
                painter = painterResource(R.drawable.star_solid),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "remove favourite"
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.star_outline),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "add favourite"
            )
        }
    }
}