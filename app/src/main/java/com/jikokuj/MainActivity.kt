package com.jikokuj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokuj.presentation.schedule.Action
import com.jikokuj.presentation.schedule.ScheduleState
import com.jikokuj.presentation.schedule.ScheduleViewModel
import com.jikokuj.presentation.schedule.queryableIsSelected
import com.jikokuj.presentation.theme.AppTheme

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
    val scheduleViewModel = viewModel<ScheduleViewModel>()

    Surface(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Map(modifier)
        Searchbar(
            modifier = modifier,
            state = scheduleViewModel.state.collectAsStateWithLifecycle().value,
            onAction = { action -> scheduleViewModel.onAction(action) }
        )
    }
}

@Composable
private fun Map(modifier: Modifier){
    Box(
        modifier = modifier.fillMaxSize()
    ) {

    }
}
@Composable
private fun Searchbar(
    modifier: Modifier,
    state: ScheduleState,
    onAction: (Action) -> Unit
){
    Row(
        modifier.padding(start = 5.dp, top = 5.dp, end = 5.dp)
            .fillMaxHeight(1/10f)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        TextField(
            modifier = modifier.fillMaxWidth(8/10f),
            value = state.searchString,
            placeholder = { Text("Megálló / járatnév") },
            onValueChange = { newVal: String ->
                onAction(Action.ChangeSearch(newVal))
            }
        )
        Spacer(modifier = modifier.weight(1f))
        Button(
            modifier = modifier.fillMaxHeight()
                .aspectRatio(1/1f),
            enabled = state.queryableIsSelected(),
            onClick = { onAction(Action.Search) }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_search_24),
                contentDescription = "Search icon",
                tint = Color.Magenta
            )
        }
    }
}