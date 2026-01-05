package com.jikokujo.schedule.presentation.schedule

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.schedule.data.model.Queryable

@Composable
fun ScheduleSearchBar(
    modifier: Modifier,
    state: ScheduleState,
    onAction: (Action) -> Unit
){
    Column(
        modifier = modifier
            .padding(all = 5.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        SearchBar(
            modifier = modifier,
            state = state,
            onAction = onAction
        )
        if (state.searchString != ""){
            DropdownMenu(
                modifier = modifier,
                state = state,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun DropdownMenu(
    modifier: Modifier,
    state: ScheduleState,
    onAction: (Action) -> Unit
){
    val filteredItems = state.filteredQueryables()
    Log.i("Filtered items", filteredItems.toString())
    Column(modifier = modifier) {
        for (i in 0..< filteredItems.count()){
            if (i < 5) {
                val currentItem = filteredItems[i]
                DropdownItem(
                    modifier = modifier,
                    item = currentItem,
                    onClick = { onAction(Action.SelectQueryable(currentItem))}
                )
            }
        }
    }
}

@Composable
private fun DropdownItem(
    modifier: Modifier,
    item: Queryable,
    onClick: () -> Unit
){
    Row(modifier = modifier) {
        when (item){
            is Queryable.Stop -> {
                Text(
                    modifier = modifier.fillMaxSize(),
                    text = item.name
                )
            }
            is Queryable.Route -> {
                Text(
                    modifier = modifier.fillMaxSize(),
                    text = item.name
                )
            }
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable(
                    onClick = { onClick.invoke() }
                )
        )
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier,
    state: ScheduleState,
    onAction: (Action) -> Unit
){
    Row(
        modifier
            .padding(start = 5.dp, top = 5.dp, end = 5.dp)
            .fillMaxHeight(1 / 15f)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        TextField(
            modifier = modifier
                .background(color = Color.Transparent)
                .fillMaxHeight()
                .fillMaxWidth(),
            value = state.searchString,
            shape = RoundedCornerShape(percent = 30),
            placeholder = { Text("Megálló / járatnév") },
            trailingIcon = {
                Icon(
                    modifier = modifier
                        .fillMaxHeight(6 / 10f)
                        .aspectRatio(1 / 1f)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Search",
                            onClick = { onAction(Action.Search) }
                        ),
                    painter = painterResource(R.drawable.baseline_search_24),
                    contentDescription = "Search icon",
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
        state = ScheduleState(),
        onAction = {}
    )
}