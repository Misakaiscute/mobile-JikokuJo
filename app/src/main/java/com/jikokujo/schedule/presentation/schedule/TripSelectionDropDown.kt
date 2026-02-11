package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.model.arrivalTimeFormatted
import com.jikokujo.schedule.data.model.getColor
import com.jikokujo.theme.Typography

@Composable
fun TripSelectionDropDown(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit,
    getRoute: (String) -> Queryable.Route,
    displayOnMap: (Trip, Queryable.Route) -> Unit
){
    val itemHeight = 40
    val maxItems = 5
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topEnd = 20f, topStart = 20f, bottomStart = 0f, bottomEnd = 20f))
            .heightIn(max = (((itemHeight + 2) * maxItems) - 2).dp)
            .verticalScroll(scrollState)
    ) {
        if (!state.dropDownExpanded) {
            TripSelectionDropDownItem(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(
                        onClick = { onAction(Action.ChangeDropDownState(true)) }
                    ),
                state = state,
                trip = state.selectedTrip,
                getRoute = getRoute,
                itemHeight = itemHeight,
                itemTextColor = MaterialTheme.colorScheme.onSurface
            )
        } else {
            for (i in 0 ..< state.trips.count()){
                val itemBackgroundColor: Color = if (state.selectedTrip?.id == state.trips[i].id){
                    MaterialTheme.colorScheme.primary
                } else if (i % 2 == 0) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }

                if (i > 0){
                    HorizontalDivider(
                        modifier = modifier,
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                TripSelectionDropDownItem(
                    modifier = modifier
                        .background(itemBackgroundColor)
                        .clickable(
                            enabled = !state.isLoading,
                            onClick = {
                                onAction(Action.SelectTrip(trip = state.trips[i]))
                                displayOnMap(
                                    state.trips[i],
                                    getRoute(state.trips[i].routeId)
                                )
                            }
                        ),
                    state = state,
                    trip = state.trips[i],
                    getRoute = getRoute,
                    itemHeight = itemHeight,
                    itemTextColor = if (state.selectedTrip?.id == state.trips[i].id) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
@Composable
private fun TripSelectionDropDownItem(
    modifier: Modifier,
    state: ScheduleSearchState,
    trip: Trip?,
    getRoute: (String) -> Queryable.Route,
    itemHeight: Int,
    itemTextColor: Color
){
    val route: Queryable.Route? = if (trip != null) getRoute(trip.routeId) else null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .background(route?.getColor("50") ?: Color.Transparent),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (trip == null){
            Text(
                text = "Nincs indulás kiválasztva!",
                style = Typography.bodyMedium.merge(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            )
        } else {
            val firstStopIdx: Int = when(state.selectedQueryable!!){
                is Queryable.Stop -> {
                    var returnIdx = 0
                    trip.stops.fastForEachIndexed { idx, stop ->
                        if (state.selectedQueryable.id == stop.id){
                            returnIdx = idx
                        }
                    }
                    returnIdx
                }
                is Queryable.Route -> 0
            }
            Text(
                modifier = Modifier.padding(horizontal = 7.dp),
                text = trip.stops[firstStopIdx].arrivalTimeFormatted() + " - " + trip.stops.last().arrivalTimeFormatted(),
                style = Typography.bodyMedium.merge(
                    color = itemTextColor,
                    textAlign = TextAlign.Center
                )
            )
            VerticalDivider(
                modifier = Modifier.padding(vertical = 3.dp),
                thickness = 1.dp,
                color = itemTextColor
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 7.dp)
                    .weight(1f),
                text = route!!.shortName + " – " + trip.headSign,
                style = Typography.bodyMedium.merge(
                    color = itemTextColor
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun TripSelectionDropDownItemPreview(){
    TripSelectionDropDownItem(
        modifier = Modifier,
        state = ScheduleSearchState(
            selectedQueryable = Queryable.Stop("0", "Zodony utca")
        ),
        trip = Trip(
            id = "TRIP_ID",
            headSign = "Gubacsi út / Határ út",
            routeId = "CM542536",
            shapeId = "YMD45",
            stops = listOf(
                StopWithLocationAndStopTime(
                    id = "0",
                    name = "Zodony utca",
                    location = Location.Stop(1.1, 1.1),
                    arrivalTime = 765,
                    order = 1
                ),
                StopWithLocationAndStopTime(
                    id = "1",
                    name = "Gubacsi út",
                    location = Location.Stop(1.2, 1.2),
                    arrivalTime = 769,
                    order = 2
                ),
                StopWithLocationAndStopTime(
                    id = "2",
                    name = "Határ út",
                    location = Location.Stop(1.3, 1.3),
                    arrivalTime = 777,
                    order = 3
                ),
            ),
            wheelchairAccessible = 1,
            bikesAllowed = 1,
            directionId = 1
        ),
        getRoute = { _ -> Queryable.Route(id = "CM542536", shortName = "119", color = "E3A4FF", type = 1) },
        itemHeight = 40,
        itemTextColor = Color.Black
    )
}