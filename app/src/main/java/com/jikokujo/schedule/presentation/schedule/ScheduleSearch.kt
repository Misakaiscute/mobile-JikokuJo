package com.jikokujo.schedule.presentation.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.Trip

@Composable
fun ScheduleSearch(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit,
    displayTripOnMap: (Trip, Queryable.Route) -> Unit,
    removeTripFromMap: () -> Unit
){
    BackHandler {
        when (state.dropDownShown){
            DropDowns.TripSelection -> {
                if (state.selectedTrip != null) {
                    onAction(Action.UnselectTrip)
                } else if (state.dropDownExpanded){
                    onAction(Action.ChangeDropDownState(false))
                } else {
                    onAction(Action.ChangeDropDownState(false, DropDowns.QueryableSelection))
                }
            }
            DropDowns.QueryableSelection -> {
                if (state.selectedQueryable != null) {
                    onAction(Action.UnselectQueryable)
                } else {
                    onAction(Action.ChangeDropDownState(false))
                }
            }
        }
    }
    Column(
        modifier = modifier
            .padding(all = 10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        when (state.dropDownShown){
            DropDowns.QueryableSelection -> {
                removeTripFromMap.invoke()
                SearchBar(
                    modifier = modifier
                        .onFocusChanged { onAction(Action.ChangeDropDownState(it.hasFocus)) }
                        .focusable(),
                    state = state,
                    onAction = onAction
                )
                if (state.dropDownExpanded) {
                    QueryableDropDown(
                        modifier = modifier,
                        state = state,
                        onAction = onAction
                    )
                }
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ExpanderArrow(
                        modifier = modifier,
                        isExpanded = state.dropDownExpanded,
                        onClick = { onAction(Action.ChangeDropDownState(!state.dropDownExpanded)) }
                    )
                    Spacer(modifier.weight(1f))
                    DateTimePicker(
                        modifier = modifier.fillMaxHeight(),
                        state = state,
                        onAction = onAction
                    )
                }
            }
            DropDowns.TripSelection -> {
                TripSelectionDropDown(
                    modifier = modifier,
                    state = state,
                    onAction = onAction,
                    displayOnMap = displayTripOnMap
                )
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    ExpanderArrow(
                        modifier = modifier,
                        isExpanded = state.dropDownExpanded,
                        onClick = { onAction(Action.ChangeDropDownState(!state.dropDownExpanded)) }
                    )
                }
            }
        }
    }
}
@Preview(showSystemUi = true)
@Composable
private fun ScheduleSearchPreview(){
    ScheduleSearch(
        modifier = Modifier,
        state = ScheduleSearchState(),
        onAction = {},
        displayTripOnMap = { _, _ -> },
        removeTripFromMap = {}
    )
}