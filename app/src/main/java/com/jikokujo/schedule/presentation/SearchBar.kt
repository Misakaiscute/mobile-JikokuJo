package com.jikokujo.schedule.presentation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.theme.Typography
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun SearchBar(
    modifier: Modifier,
    state: ScheduleSearchState,
    onAction: (ScheduleAction) -> Unit
){
    val height = 50.dp
    Row(
        modifier = modifier
            .onFocusChanged {
                onAction(ScheduleAction.ChangeDropDownState(it.hasFocus && state.queryables.count() > 0))
            }
            .focusable()
            .height(height)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        val trailingIcon: Int? = if (state.selectedQueryable != null) {
            R.drawable.baseline_search_24
        } else if (state.searchString.isNotBlank()){
            R.drawable.trashcan
        } else null

        CustomSearchBar(
            modifier = modifier.fillMaxSize(),
            text = state.searchString,
            placeholder = "Megálló / járatnév",
            trailingIcon = trailingIcon,
            onTrailingIconClick = {
                if (state.selectedQueryable == null) {
                    onAction(ScheduleAction.UnselectQueryable)
                } else {
                    onAction(ScheduleAction.Search)
                }
            },
            trailingIconDesc = if (state.selectedQueryable == null) "keresés törlése" else "keresés",
            onValueChange = { newVal ->
                onAction(ScheduleAction.ChangeSearch(newVal))
            },
        )
    }
}
@Composable
private fun CustomSearchBar(
    modifier: Modifier,
    text: String,
    placeholder: String,
    @DrawableRes trailingIcon: Int?,
    trailingIconDesc: String = "",
    onTrailingIconClick: () -> Unit,
    onValueChange: (String) -> Unit,
){
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text)) }

    LaunchedEffect(text) {
        if (text != textFieldValue.text) {
            textFieldValue = TextFieldValue(text, selection = TextRange(text.length))
        }
    }

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
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onValueChange(newValue.text)
        },
        textStyle = Typography.bodyMedium.merge(
            color = MaterialTheme.colorScheme.onSurface
        ),
        visualTransformation = VisualTransformation.None,
        singleLine = true,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = textFieldValue.text,
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
                    if (trailingIcon != null){
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
                }
            )
        }
    )
}