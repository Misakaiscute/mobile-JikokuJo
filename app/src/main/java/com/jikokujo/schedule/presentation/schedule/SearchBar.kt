package com.jikokujo.schedule.presentation.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.theme.Typography

@Composable
fun SearchBar(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (Action) -> Unit
){
    val height = 50.dp
    Row(
        modifier = modifier
            .onFocusChanged { onAction(Action.ChangeDropDownState(it.hasFocus)) }
            .focusable()
            .height(height)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = modifier
                .background(Color.Transparent)
                .fillMaxHeight()
                .fillMaxWidth(),
            value = state.searchString,
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
                    text = "Megálló / járatnév",
                    style = Typography.bodyMedium.merge(
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            onValueChange = { newVal: String ->
                onAction(Action.ChangeSearch(newVal))
            },
        )
    }
}