package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jikokujo.R
import com.jikokujo.theme.Typography
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun SearchBar(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    val height = 50.dp
    val searchString = remember { MutableStateFlow(state.searchString) }

    LaunchedEffect(searchString) {
        searchString
            .debounce(300)
            .collect { s ->
                onAction(Action.ChangeSearch(s))
            }
    }
    LaunchedEffect(state.selectedQueryable) {
        if (searchString.value != state.searchString){
            searchString.value = state.searchString
        }
    }

    Row(
        modifier = modifier
            .onFocusChanged {
                onAction(Action.ChangeDropDownState(it.hasFocus && state.queryables.count() > 0))
            }
            .focusable()
            .height(height)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = modifier.fillMaxSize(),
            value = searchString.collectAsStateWithLifecycle().value,
            singleLine = true,
            textStyle = Typography.bodyMedium.merge(
                color = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(bottomStart = 0f, bottomEnd = 0f, topStart = 20f, topEnd = 20f),
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            placeholder = {
                Text(
                    modifier = modifier.fillMaxHeight(),
                    text = "Megálló / járatnév",
                    style = Typography.bodyMedium.merge(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            trailingIcon = {
                if (state.selectedQueryable != null){
                    Icon(
                        modifier = modifier
                            .fillMaxHeight(4/5f)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = "Search",
                                onClick = { onAction(Action.Search) }
                            ),
                        painter = painterResource(R.drawable.baseline_search_24),
                        contentDescription = "select",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Icon(
                        modifier = modifier
                            .fillMaxHeight(4/5f)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = "Unselect queryable",
                                onClick = { onAction(Action.UnselectQueryable) }
                            ),
                        painter = painterResource(R.drawable.trashcan),
                        contentDescription = "unselect",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            onValueChange = { newVal: String -> searchString.value = newVal },
        )
    }
}