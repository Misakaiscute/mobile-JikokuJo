package com.jikokujo.schedule.presentation.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.core.utils.loadingShimmer
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.Trip

@Composable
fun ScheduleSearch(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit,
    getRoute: (String) -> Queryable.Route,
    displayTripOnMap: (Trip, Queryable.Route, Queryable) -> Unit,
    removeTripFromMap: () -> Unit
){
    BackHandler {
        when (state.dropDownShown){
            DropDowns.TripSelection -> {
                if (state.selectedTrip != null) {
                    removeTripFromMap()
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
        if (state.isLoading){
            Row(
                modifier = modifier
                    .height(70.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier
                    .fillMaxSize()
                    .loadingShimmer(
                        durationMillis = 1500,
                        background = MaterialTheme.colorScheme.surface
                    )
                )
            }
        } else {
            when (state.dropDownShown){
                DropDowns.QueryableSelection -> {
                    removeTripFromMap()
                    SearchBar(
                        modifier = modifier,
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
                            modifier = modifier.fillMaxHeight(),
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
                        getRoute = getRoute,
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
}
@Preview(showSystemUi = true)
@Composable
private fun ScheduleSearchPreview(){
    ScheduleSearch(
        modifier = Modifier,
        state = ScheduleSearchState(),
        onAction = {},
        getRoute = { _ -> Queryable.Route("001", "M3-mas metró", "0b6324", 3) },
        displayTripOnMap = { _, _, _ -> },
        removeTripFromMap = {}
    )
}