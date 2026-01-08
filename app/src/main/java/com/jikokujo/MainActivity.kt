package com.jikokujo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokujo.schedule.presentation.map.DisplayMapsforgeMap
import com.jikokujo.schedule.presentation.map.MapViewModel
import com.jikokujo.schedule.presentation.schedule.ScheduleSearchBar
import com.jikokujo.schedule.presentation.schedule.ScheduleSearchViewModel
import com.jikokujo.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                ScheduleScreen(Modifier)
            }
        }
    }
}

@Composable
private fun ScheduleScreen(modifier: Modifier){
    val scheduleSearchViewModel = viewModel<ScheduleSearchViewModel>()
    val mapViewModel = viewModel<MapViewModel>()
    Surface(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        DisplayMapsforgeMap(
            modifier = modifier,
            state = mapViewModel.state.collectAsStateWithLifecycle().value,
            onAction = { action -> mapViewModel.onAction(action) }
        )
        ScheduleSearchBar(
            modifier = modifier,
            state = scheduleSearchViewModel.state.collectAsStateWithLifecycle().value,
            onAction = { action -> scheduleSearchViewModel.onAction(action) }
        )
    }
}