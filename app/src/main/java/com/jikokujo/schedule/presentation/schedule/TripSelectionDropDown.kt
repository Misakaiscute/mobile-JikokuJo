package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
    onAction: (Action) -> Any,
    displayOnMap: (Trip) -> Unit
){
    val itemHeight = 40
    val maxItems = 5
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 20f, bottomEnd = 0f, topStart = 0f, topEnd = 0f))
            .heightIn((((itemHeight + 1) * maxItems) - 1).dp)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            )
    ) {
        for (i in 0 ..< state.trips.count()){
            val itemBackgroundColor: Color = if (state.selectedTrip?.id == state.trips[i].id){
                Color.Cyan
            } else if (i % 2 == 0) {
                Color.LightGray
            } else {
                Color.White
            }
            if (i > 0){
                HorizontalDivider(
                    modifier = modifier,
                    thickness = 1.dp,
                    color = Color.Black
                )
            }
            TripSelectionDropDownItem(
                modifier = modifier
                    .background(itemBackgroundColor)
                    .clickable(
                        enabled = !state.isLoading,
                        onClick = {
                            onAction(Action.SelectTrip(state.trips[i]))
                            displayOnMap(state.selectedTrip!!)
                        }
                    ),
                state = state,
                item = state.trips[i],
                route = onAction(Action.GetRoute(state.trips[i].routeId)) as Queryable.Route,
                itemHeight = itemHeight
            )
        }
    }
}
@Composable
private fun TripSelectionDropDownItem(
    modifier: Modifier,
    state: ScheduleSearchState,
    route: Queryable.Route,
    item: Trip,
    itemHeight: Int
){
    val overscrollEffect = rememberOverscrollEffect()
    val firstStopIdx: Int = when(state.selectedQueryable!!){
        is Queryable.Stop -> {
            var returnIdx = 0
            item.stops.fastForEachIndexed { idx, stop ->
                if (state.selectedQueryable.id == stop.id){
                    returnIdx = idx
                }
            }
            returnIdx
        }
        is Queryable.Route -> 0
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .background(route.getColor("80"))
            .overscroll(overscrollEffect),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 7.dp),
            text = item.stops[firstStopIdx].arrivalTimeFormatted() + " - " + item.stops.last().arrivalTimeFormatted(),
            style = Typography.bodyLarge.merge(
                textAlign = TextAlign.Center
            )
        )
        VerticalDivider(
            modifier = Modifier.padding(vertical = 3.dp),
            thickness = 1.dp,
            color = Color.Black
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 7.dp)
                .weight(1f),
            text = route.shortName + " – " + item.headSign,
            style = Typography.bodyLarge
        )
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
        item = Trip(
            id = "TRIP_ID",
            headSign = "Gubacsi út / Határ út",
            routeId = "CM542536",
            shapeId = "YMD45",
            stops = listOf(
                StopWithLocationAndStopTime(
                    id = "0",
                    name = "Zodony utca",
                    location = Location.Stop(1.1, 1.1),
                    arrivalTime = 765
                ),
                StopWithLocationAndStopTime(
                    id = "1",
                    name = "Gubacsi út",
                    location = Location.Stop(1.2, 1.2),
                    arrivalTime = 769
                ),
                StopWithLocationAndStopTime(
                    id = "2",
                    name = "Határ út",
                    location = Location.Stop(1.3, 1.3),
                    arrivalTime = 777
                ),
            ),
            wheelchairAccessible = 1,
            bikesAllowed = 1,
            directionId = 1
        ),
        route = Queryable.Route(id = "CM542536", shortName = "119", color = "E3A4FF", type = 1),
        itemHeight = 40
    )
}