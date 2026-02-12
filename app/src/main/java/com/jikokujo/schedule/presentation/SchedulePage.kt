package com.jikokujo.schedule.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokujo.schedule.presentation.map.Action as MapAction
import com.jikokujo.schedule.presentation.map.DisplayMapsforgeMap
import com.jikokujo.schedule.presentation.map.MapViewModel
import com.jikokujo.schedule.presentation.schedule.Action as ScheduleAction
import com.jikokujo.schedule.presentation.schedule.ScheduleSearch
import com.jikokujo.schedule.presentation.schedule.ScheduleSearchState
import com.jikokujo.schedule.presentation.schedule.ScheduleSearchViewModel
import com.jikokujo.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun SchedulePage(modifier: Modifier){
    val scheduleSearchViewModel = viewModel<ScheduleSearchViewModel>()
    val mapViewModel = viewModel<MapViewModel>()
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        if (scheduleSearchViewModel.state.collectAsStateWithLifecycle().value.error != null){
            OnError(
                modifier = Modifier,
                state = scheduleSearchViewModel.state.collectAsStateWithLifecycle().value,
                onAction = { action ->
                    scheduleSearchViewModel.viewModelScope.launch{
                        scheduleSearchViewModel.onAction(action)
                    }
                }
            )
        } else {
            DisplayMapsforgeMap(
                modifier = Modifier,
                state = mapViewModel.state.collectAsStateWithLifecycle().value,
                layerState = mapViewModel.mapLayerState.collectAsStateWithLifecycle().value,
                onAction = { action ->
                    mapViewModel.viewModelScope.launch {
                        mapViewModel.onAction(action)
                    }
                }
            )
            ScheduleSearch(
                modifier = Modifier,
                state = scheduleSearchViewModel.state.collectAsStateWithLifecycle().value,
                onAction = { action ->
                    scheduleSearchViewModel.viewModelScope.launch {
                        scheduleSearchViewModel.onAction(action)
                    }
                },
                getRoute = { routeId ->
                    scheduleSearchViewModel.getRoute(routeId)
                },
                displayTripOnMap = { trip, routeAssoc, selectedThrough ->
                    mapViewModel.viewModelScope.launch {
                        mapViewModel.onAction(
                            MapAction.SelectTrip(
                                trip = trip,
                                routeAssociated = routeAssoc,
                                selectedThrough = selectedThrough
                            )
                        )
                    }
                },
                removeTripFromMap = {
                    mapViewModel.viewModelScope.launch {
                        mapViewModel.onAction(MapAction.UnselectTrip)
                    }
                }
            )
        }
    }
}
@Composable
private fun OnError(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (ScheduleAction) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Button(
            shape = RoundedCornerShape(10),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = modifier
                .fillMaxWidth(2/3f)
                .align(Alignment.Center),
            onClick = {
                onAction(ScheduleAction.RetryFetchInitialData)
            }
        ) {
            Text(
                text = state.error!!,
                style = Typography.bodyLarge.merge(
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}