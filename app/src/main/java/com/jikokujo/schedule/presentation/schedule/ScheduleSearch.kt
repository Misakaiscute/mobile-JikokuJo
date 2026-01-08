package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.getIconForType
import com.jikokujo.theme.Typography
import java.time.LocalDate
import kotlin.math.min

@Composable
fun ScheduleSearchBar(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    Column(
        modifier = modifier
            .padding(all = 10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        SearchBar(
            modifier = modifier
                .onFocusChanged { onAction(Action.ChangeDropdownState(it.hasFocus)) }
                .focusable(),
            state = state,
            onAction = onAction
        )
        if (state.searchString != "" && state.selectedQueryable == null && state.filteredQueryablesDropdownExpanded){
            DropdownMenu(
                modifier = modifier,
                state = state,
                onAction = onAction
            )
        }
        DateTimePicker(
            modifier = modifier,
            state = state,
            onAction = onAction
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePicker(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    val timeInputState = rememberTimePickerState(
        initialHour = state.tripsFrom.hour,
        initialMinute = state.tripsFrom.minute,
        is24Hour = true
    )
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = LocalDate.of(
            state.tripsFrom.year,
            state.tripsFrom.monthValue,
            state.tripsFrom.dayOfMonth
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
                text = state.tripsFrom.year.toString() + '.' +
                       state.tripsFrom.monthValue.toString().padStart(2, '0') + '.' +
                       state.tripsFrom.dayOfMonth.toString().padStart(2, '0'),
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
                text = state.tripsFrom.hour.toString().padStart(2, '0') + ':' +
                       state.tripsFrom.minute.toString().padStart(2, '0'),
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
@Composable
private fun DropdownMenu(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    val itemHeight = 40
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(size = 5.dp))
            .heightIn(0.dp, (itemHeight * 5).dp)
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical
            )
    ) {
        for (i in 0..state.filteredQueryables.count()){
            DropdownItem(
                modifier = modifier.background(if (i % 2 == 0) Color.LightGray else Color.White),
                item = state.filteredQueryables[i],
                onClick = { onAction(Action.SelectQueryable(state.filteredQueryables[i])) },
                itemHeight = itemHeight
            )
        }
    }
}

@Composable
private fun DropdownItem(
    modifier: Modifier,
    item: Queryable,
    onClick: () -> Unit,
    itemHeight: Int
){
    val overscrollEffect = rememberOverscrollEffect()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .clickable(
                onClick = { onClick.invoke() }
            )
            .overscroll(overscrollEffect),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (item){
            is Queryable.Stop -> {
                Icon(
                    modifier = Modifier.fillMaxWidth(1/10f),
                    painter = painterResource(R.drawable.busstop),
                    contentDescription = "bus stop",
                    tint = Color.DarkGray
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                )
            }
            is Queryable.Route -> {
                Icon(
                    modifier = Modifier.fillMaxWidth(1/10f),
                    painter = painterResource(item.getIconForType()),
                    contentDescription = "other transport icon",
                    tint = Color.DarkGray
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                )
            }
        }
    }
}
@Composable
private fun SearchBar(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    Row(
        modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(Color.White),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = modifier
                .background(color = Color.Transparent)
                .fillMaxHeight()
                .fillMaxWidth(),
            value = state.searchString,
            textStyle = Typography.bodyMedium.merge(
                TextStyle(textAlign = TextAlign.Center)
            ),
            shape = RoundedCornerShape(bottomStart = 0f, bottomEnd = 0f, topStart = 20f, topEnd = 20f),
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            placeholder = {
                Text(
                    text = "Megálló / járatnév",
                    style = Typography.bodyMedium.merge(
                        TextStyle(textAlign = TextAlign.Center)
                    )
                )
            },
            trailingIcon = {
                Icon(
                    modifier = modifier
                        .fillMaxHeight(7/10f)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = if (state.selectedQueryable != null) "Delete search" else "Search",
                            onClick = {
                                onAction(if (state.selectedQueryable != null) Action.UnselectQueryable else Action.Search)
                            }
                        ),
                    painter = painterResource(
                        if (state.selectedQueryable != null) R.drawable.trashcan else R.drawable.baseline_search_24
                    ),
                    contentDescription = "button",
                    tint = Color.DarkGray
                )
            },
            onValueChange = { newVal: String ->
                onAction(Action.ChangeSearch(newVal))
            },
        )
    }
}
@Preview(showSystemUi = true)
@Composable
private fun ScheduleSearchBarPreview(){
    ScheduleSearchBar(
        modifier = Modifier,
        state = ScheduleSearchState(),
        onAction = {}
    )
}
@Preview(showBackground = true)
@Composable
private fun DropdownItemPreview(){
    DropdownItem(
        modifier = Modifier,
        item = Queryable.Route("001", "M3-mas metró", 3),
        onClick = {},
        itemHeight = 40
    )
}