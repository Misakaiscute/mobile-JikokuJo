package com.jikokujo.schedule.presentation.schedule

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.VisualTransformation
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
    LaunchedEffect(state.searchString) {
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
        CustomSearchBar(
            modifier = modifier.fillMaxSize(),
            text = searchString.collectAsStateWithLifecycle().value,
            placeholder = "Megálló / járatnév",
            trailingIcon = if (state.selectedQueryable == null) R.drawable.trashcan else R.drawable.baseline_search_24,
            onTrailingIconClick = {
                if (state.selectedQueryable == null) {
                    onAction(Action.UnselectQueryable)
                } else {
                    onAction(Action.Search)
                }
            },
            trailingIconDesc = if (state.selectedQueryable == null) "keresés törlése" else "keresés",
            onValueChange = { newVal -> searchString.value = newVal },
        )
    }
}
@Composable
private fun CustomSearchBar(
    modifier: Modifier,
    text: String,
    placeholder: String,
    @DrawableRes trailingIcon: Int,
    trailingIconDesc: String = "",
    onTrailingIconClick: () -> Unit,
    onValueChange: (String) -> Unit,
){
    val interactionSource = remember { MutableInteractionSource() }
    val colors = TextFieldDefaults.colors().copy(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        errorContainerColor = MaterialTheme.colorScheme.surface,
        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        errorTextColor = MaterialTheme.colorScheme.error
    )
    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = onValueChange,
        textStyle = Typography.bodyMedium.merge(
            color = MaterialTheme.colorScheme.onSurface
        ),
        visualTransformation = VisualTransformation.None,
        singleLine = true,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = text,
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium.merge(
                            color = Color.DarkGray
                        ),
                        maxLines = 1
                    )
                },
                singleLine = true,
                enabled = true,
                colors = colors,
                contentPadding = PaddingValues(
                    start = 10.dp,
                    end = 0.dp,
                    top = 0.dp,
                    bottom = 0.dp
                ),
                interactionSource = interactionSource,
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .fillMaxHeight(2/3f)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = trailingIconDesc,
                                onClick = onTrailingIconClick
                            ),
                        painter = painterResource(trailingIcon),
                        contentDescription = trailingIconDesc,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    )
}