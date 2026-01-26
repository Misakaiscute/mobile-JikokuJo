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
import com.jikokujo.schedule.presentation.ScheduleScreen
import com.jikokujo.schedule.presentation.map.Action
import com.jikokujo.schedule.presentation.map.DisplayMapsforgeMap
import com.jikokujo.schedule.presentation.map.MapViewModel
import com.jikokujo.schedule.presentation.schedule.ScheduleSearch
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