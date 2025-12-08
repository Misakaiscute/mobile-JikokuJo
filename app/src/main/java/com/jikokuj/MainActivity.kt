package com.jikokuj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokuj.presentation.schedule.ScheduleViewModel
import com.jikokuj.presentation.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Main()
            }
        }
    }
}

@Composable
private fun Main(){
    val scheduleViewModel = viewModel<ScheduleViewModel>()
    Box(Modifier.fillMaxSize()){
        TextField(
            value = scheduleViewModel.queryState.collectAsState().value,
            onValueChange = { value -> scheduleViewModel.changeSearch(value) }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainPreview(){
    AppTheme {
        Main()
    }
}