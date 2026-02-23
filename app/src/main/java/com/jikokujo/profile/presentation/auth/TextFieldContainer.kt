package com.jikokujo.profile.presentation.auth

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.jikokujo.theme.Typography

@Composable
fun TextFieldContainer(
    modifier: Modifier,
    text: String,
    label: String,
    hasError: Boolean = false,
    isPasswordField: Boolean = false,
    onValueChange: (String) -> Unit,
    onImeAction: () -> Unit,
    isLastAction: Boolean = false
){
    val interactionSource = remember { MutableInteractionSource() }
    val colors = OutlinedTextFieldDefaults.colors().copy(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        errorContainerColor = MaterialTheme.colorScheme.surface,
        focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        errorTextColor = MaterialTheme.colorScheme.error
    )
    Spacer(modifier = Modifier.height(4.dp))
    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = onValueChange,
        textStyle = Typography.bodyMedium.merge(
            color = MaterialTheme.colorScheme.onSurface
        ),
        visualTransformation = if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            imeAction = if (isLastAction) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        singleLine = true,
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = text,
                visualTransformation = if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
                innerTextField = innerTextField,
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.merge(
                            color = Color.DarkGray
                        ),
                        maxLines = 1
                    )
                },
                singleLine = true,
                enabled = true,
                isError = hasError,
                colors = colors,
                contentPadding = PaddingValues(
                    start = 10.dp,
                    end = 0.dp,
                    top = 0.dp,
                    bottom = 0.dp
                ),
                interactionSource = interactionSource
            )
        }
    )
    Spacer(modifier = Modifier.height(4.dp))
}