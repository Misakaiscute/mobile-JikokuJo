package com.jikokujo.profile.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokujo.theme.Typography

@Composable
fun Register(modifier: Modifier){
    val registerViewModel = viewModel<RegisterViewModel>()
    val focusManager = LocalFocusManager.current

    val state = registerViewModel.state.collectAsStateWithLifecycle().value
    val heightModifier = modifier
        .fillMaxWidth()
        .height(50.dp)

    Column(
        modifier = heightModifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            text = "Regisztráció",
            style = Typography.titleLarge.merge(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = modifier.height(10.dp))
        Row(
            modifier = heightModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextFieldContainer(
                modifier = modifier.fillMaxHeight(),
                text = state.firstName,
                name = "Keresztnév",
                hasError = state.inputError is InputException.MissingFieldException && state.firstName.isBlank(),
                onValueChange = { value ->
                    registerViewModel.onAction(Action.ChangeValue(
                        state.copy(firstName = value)
                    ))
                },
                imeAction = { focusManager.moveFocus(FocusDirection.Right) },
            )
            Spacer(modifier = modifier.weight(1f))
            TextFieldContainer(
                modifier = modifier.fillMaxHeight(),
                text = state.firstName,
                name = "Vezetéknév",
                hasError = state.inputError is InputException.MissingFieldException && state.lastName.isBlank(),
                onValueChange = { value ->
                    registerViewModel.onAction(Action.ChangeValue(
                        state.copy(lastName = value)
                    ))
                },
                imeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )
        }

        TextFieldContainer(
            modifier = heightModifier,
            text = state.email,
            name = "Email cím",
            hasError = state.inputError is InputException.InvalidEmailException || (state.inputError is InputException.MissingFieldException && state.email.isBlank()),
            onValueChange = { value ->
                registerViewModel.onAction(Action.ChangeValue(
                    state.copy(email = value)
                ))
            },
            imeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        val passwordHasError = state.inputError is InputException.InvalidPasswordException || state.inputError is InputException.PasswordsNotMatchingException
        TextFieldContainer(
            modifier = modifier,
            text = state.password,
            name = "Jelszó",
            hasError = passwordHasError || (state.inputError is InputException.MissingFieldException && state.email.isBlank()),
            isPasswordField = true,
            onValueChange = { value ->
                registerViewModel.onAction(Action.ChangeValue(
                    state.copy(password = value)
                ))
            },
            imeAction = { focusManager.moveFocus(FocusDirection.Down) },
        )
        TextFieldContainer(
            modifier = heightModifier,
            text = state.passwordConfirmation,
            name = "Jelszó újra",
            hasError = passwordHasError || (state.inputError is InputException.MissingFieldException && state.email.isBlank()),
            isPasswordField = true,
            onValueChange = { value ->
                registerViewModel.onAction(Action.ChangeValue(
                    state.copy(password = value)
                ))
            },
            imeAction = { focusManager.moveFocus(FocusDirection.Down) },
        )
        InfoText(
            modifier = modifier,
            state = state
        )
        Row(
            modifier = heightModifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = modifier
                    .fillMaxHeight()
                    .fillMaxWidth(4/5f),
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                enabled = !state.isLoading,
                onClick = { registerViewModel.onAction(Action.Submit) }
            ){
                Text(
                    text = "Regisztráció",
                    style = Typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun InfoText(
    modifier: Modifier,
    state: RegisterState
){
    val text: String? = state.submitError
        ?: if (state.inputError != null) {
            state.inputError.message ?: "Valami hiba történt."
        } else if (state.successfulRegistration){
            "Sikeres regisztráció"
        } else null

    if (text == null){
        Spacer(modifier = modifier.height(40.dp))
    } else {
        Spacer(modifier = modifier.height(5.dp))
        Text(
            modifier = modifier
                .fillMaxWidth()
                .height(30.dp),
            text = text,
            style = Typography.bodySmall.merge(
                color = if (state.successfulRegistration) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = modifier.height(5.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview(){
    Register(modifier = Modifier)
}