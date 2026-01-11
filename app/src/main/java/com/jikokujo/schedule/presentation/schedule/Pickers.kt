package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jikokujo.theme.Typography
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    val timeInputState = rememberTimePickerState(
        initialHour = state.tripTimeConstraint.hour,
        initialMinute = state.tripTimeConstraint.minute,
        is24Hour = true
    )
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = LocalDate.of(
            state.tripTimeConstraint.year,
            state.tripTimeConstraint.monthValue,
            state.tripTimeConstraint.dayOfMonth
        )
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth(1/4f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(bottomStart = 50f, bottomEnd = 0f, topStart = 0f, topEnd = 0f))
                .background(Color.White)
                .wrapContentSize(Alignment.Center),
            text = "Utazás:",
            style = Typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = modifier,
            shape = RoundedCornerShape(size = 0.dp),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            onClick = { onAction(Action.ShowDialog(Dialogs.DatePicker)) }
        ) {
            Text(
                modifier = modifier.fillMaxHeight(),
                text = state.tripTimeConstraint.year.toString() + '.' +
                        state.tripTimeConstraint.monthValue.toString().padStart(2, '0') + '.' +
                        state.tripTimeConstraint.dayOfMonth.toString().padStart(2, '0'),
                style = Typography.bodyMedium,
            )
        }
        Button(
            modifier = modifier.fillMaxHeight(),
            shape = RoundedCornerShape(bottomStart = 0f, bottomEnd = 50f, topStart = 0f, topEnd = 0f),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            onClick = { onAction(Action.ShowDialog(Dialogs.TimePicker)) }
        ) {
            Text(
                modifier = modifier.fillMaxHeight(),
                text = state.tripTimeConstraint.hour.toString().padStart(2, '0') + ':' +
                        state.tripTimeConstraint.minute.toString().padStart(2, '0'),
                style = Typography.bodyMedium,
            )
        }
    }
    when(state.shownDialog){
        Dialogs.TimePicker -> {
            PickerDialog(
                onDismiss = { onAction(Action.ShowDialog(null)) },
                onConfirm = { onAction(Action.ChangeFromTime(
                    hour = timeInputState.hour,
                    minute = timeInputState.minute
                )) }
            ) {
                TimeInput(timeInputState)
            }
        }
        Dialogs.DatePicker -> {
            PickerDialog(
                onDismiss = { onAction(Action.ShowDialog(null)) },
                onConfirm = { onAction(Action.ChangeFromDate(
                    year = datePickerState.getSelectedDate()!!.year,
                    month = datePickerState.getSelectedDate()!!.monthValue,
                    day = datePickerState.getSelectedDate()!!.dayOfMonth
                )) }
            ) {
                DatePicker(datePickerState)
            }
        }
        else -> {}
    }
}
@Composable
private fun PickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bezárás")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        text = { content() }
    )
}