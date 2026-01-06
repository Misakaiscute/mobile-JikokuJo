package com.jikokujo.schedule.presentation.schedule

import android.util.Log
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.getIconForType

@Composable
fun ScheduleSearchBar(
    modifier: Modifier,
    state: ScheduleState,
    onAction: (Action) -> Unit
){
    var searchBarFocused by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .padding(all = 10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        SearchBar(
            modifier = modifier
                .onFocusChanged { searchBarFocused = it.hasFocus }
                .focusable(),
            state = state,
            onAction = onAction
        )
        if (state.searchString != "" && searchBarFocused){
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
    Column(
        modifier = modifier.clip(RoundedCornerShape(percent = 30))
    ) {
        for (i in 0..< state.filteredQueryables.count()){
            if (i < 5) {
                val currentItem = state.filteredQueryables[i]
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
    val overscrollEffect = rememberOverscrollEffect()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White)
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
                    modifier = modifier.fillMaxWidth(1/10f),
                    painter = painterResource(R.drawable.busstop),
                    contentDescription = "buszmegálló",
                    tint = Color.DarkGray
                )
                Text(
                    modifier = modifier.weight(1f),
                    text = item.name,
                )
            }
            is Queryable.Route -> {
                Icon(
                    modifier = modifier.fillMaxWidth(1/10f),
                    painter = painterResource(item.getIconForType()),
                    contentDescription = "tömegközlekedési eszköz jele",
                    tint = Color.DarkGray
                )
                Text(
                    modifier = modifier.weight(1f),
                    text = item.name,
                )
            }
        }
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
            .height(50.dp)
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
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            placeholder = { Text("Megálló / járatnév") },
            trailingIcon = {
                if (state.selectedQueryable == null) {
                    Icon(
                        modifier = modifier
                            .fillMaxHeight(3/5f)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = "Delete search",
                                onClick = { onAction(Action.UnselectQueryable) }
                            ),
                        painter = painterResource(R.drawable.trashcan),
                        contentDescription = "trashcan icon",
                        tint = Color.DarkGray
                    )
                } else {
                    Icon(
                        modifier = modifier
                            .fillMaxHeight(3/5f)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = "Search",
                                onClick = { onAction(Action.Search) }
                            ),
                        painter = painterResource(R.drawable.baseline_search_24),
                        contentDescription = "Search icon",
                        tint = Color.DarkGray
                    )
                }
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

@Preview(showBackground = true)
@Composable
private fun DropdownItemPreview(){
    DropdownItem(
        modifier = Modifier,
        item = Queryable.Route("001", "M3-mas metró", 3),
        onClick = {}
    )
}