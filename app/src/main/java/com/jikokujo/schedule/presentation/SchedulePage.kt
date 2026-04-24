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
import com.jikokujo.map.presentation.Map
import com.jikokujo.map.presentation.TripInfoViewModel
import com.jikokujo.theme.Typography
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun SchedulePage(modifier: Modifier){
    val scheduleSearchViewModel = viewModel<ScheduleSearchViewModel>()
    val tripInfoViewModel = viewModel<TripInfoViewModel>()
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        if (scheduleSearchViewModel.state.collectAsStateWithLifecycle().value.error.isNotEmpty()){
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
            Map(
                modifier = Modifier,
                state = tripInfoViewModel.state.collectAsStateWithLifecycle().value,
                onAction = { action ->
                    tripInfoViewModel.viewModelScope.launch {
                        tripInfoViewModel.onAction(action)
                    }
                }
            )
            ScheduleSearch(
                modifier = Modifier,
                state = scheduleSearchViewModel.state.collectAsStateWithLifecycle().value,
                onScheduleAction = { action ->
                    scheduleSearchViewModel.viewModelScope.launch {
                        scheduleSearchViewModel.onAction(action)
                    }
                },
                onTripAction = { action ->
                    tripInfoViewModel.viewModelScope.launch {
                        tripInfoViewModel.onAction(action)
                    }
                },
                getRoute = { routeId -> runBlocking {
                    scheduleSearchViewModel.getRoute(routeId)
                }},
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
            val text: String = try {
                state.error.first().onError ?: "Valami hiba történt."
            } catch (_: Exception) {
                "Valami hiba történt."
            }
            Text(
                text = "$text Újrapróbálás?",
                style = Typography.bodyLarge.merge(
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}