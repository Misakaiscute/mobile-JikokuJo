package com.jikokujo.profile.presentation.auth

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.jikokujo.theme.Typography

@Composable
fun TextFieldContainer(
    modifier: Modifier,
    text: String,
    name: String,
    hasError: Boolean = false,
    isPasswordField: Boolean = false,
    onValueChange: (String) -> Unit,
    imeAction: () -> Unit,
    isLastAction: Boolean = false
){
    OutlinedTextField(
        modifier = modifier,
        value = text,
        label = @Composable {
            Text(
                text = name,
                style = Typography.bodyMedium.merge(
                    color = Color.DarkGray
                ),
                maxLines = 1
            )
        },
        placeholder = @Composable {
            Text(
                text = name,
                style = Typography.bodyMedium.merge(
                    color = MaterialTheme.colorScheme.primary
                ),
                maxLines = 1
            )
        },
        textStyle = Typography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            errorTextColor = MaterialTheme.colorScheme.error
        ),
        singleLine = true,
        isError = hasError,
        visualTransformation = if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            imeAction = if (isLastAction) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { imeAction.invoke() },
            onDone = { imeAction.invoke() }
        ),
        onValueChange = { value -> onValueChange(value) }
    )
}