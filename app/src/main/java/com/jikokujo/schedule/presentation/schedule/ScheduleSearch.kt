package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.theme.Typography

@Composable
fun ScheduleSearchBar(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit,
    displayOnMap: (Trip) -> Unit
){
    Column(
        modifier = modifier
            .padding(all = 10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        SearchBar(
            modifier = modifier
                .onFocusChanged { onAction(Action.ChangeDropDownState(it.hasFocus)) }
                .focusable(),
            state = state,
            onAction = onAction
        )
        if (state.dropDownExpanded){
            when (state.dropDownShown){
                DropDowns.QueryableSelection -> {
                    QueryableDropDown(
                        modifier = modifier,
                        state = state,
                        onAction = onAction
                    )
                    DateTimePicker(
                        modifier = modifier,
                        state = state,
                        onAction = onAction
                    )
                }
                DropDowns.TripSelection -> {
                    TripSelectionDropDown(
                        modifier = modifier,
                        state = state,
                        onAction = onAction,
                        displayOnMap = displayOnMap
                    )
                }
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
            textStyle = Typography.bodyMedium,
            shape = RoundedCornerShape(bottomStart = 0f, bottomEnd = 0f, topStart = 20f, topEnd = 20f),
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            placeholder = {
                Text(
                    text = "Megálló / járatnév",
                    style = Typography.bodyMedium
                )
            },
            trailingIcon = {
                Icon(
                    modifier = modifier
                        .fillMaxHeight(3/5f)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Search",
                            onClick = {
                                onAction(Action.Search)
                            }
                        ),
                    painter = painterResource(
                        R.drawable.baseline_search_24
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
        onAction = {},
        displayOnMap = {}
    )
}