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
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jikokujo.schedule.data.model.Trip

@Composable
fun TripSelectionDropDown(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    val itemHeight = 40
    val maxItems = 5
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 20f, bottomEnd = 0f, topStart = 0f, topEnd = 0f))
            .heightIn(0.dp, (((itemHeight + 1) * maxItems) - 1).dp)
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
                modifier = modifier.background(itemBackgroundColor),
                item = state.trips[i],
                onClick = { onAction(Action.SelectTrip(state.trips[i])) },
                itemHeight = itemHeight
            )
        }
    }
}
@Composable
private fun TripSelectionDropDownItem(
    modifier: Modifier,
    item: Trip,
    onClick: () -> Unit,
    itemHeight: Int
){
    val overscrollEffect = rememberOverscrollEffect()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .clickable(
                onClick = onClick
            )
            .overscroll(overscrollEffect),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(2/5f),
            text = item.stops[0].name!!, //TODO: THIS NEED TO BE: TIME OF THE FIRST STOP OR THE TIME OF THE FIRST SELECTED STOP - LAST STOP
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f),
            text = item.shortName,
        )
    }
}