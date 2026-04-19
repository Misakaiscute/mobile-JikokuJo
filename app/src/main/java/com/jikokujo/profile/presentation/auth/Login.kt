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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokujo.theme.AppTheme
import com.jikokujo.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun Login(
    modifier: Modifier,
    onAuthSuccess: () -> Unit
){
    val loginViewModel = viewModel<LoginViewModel>()

    LoginContent(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        state = loginViewModel.state.collectAsStateWithLifecycle().value,
        onAction = { action ->
            loginViewModel.viewModelScope.launch {
                loginViewModel.onAction(action)
            }
        },
        onAuthSuccess = onAuthSuccess
    )
}

@Composable
private fun LoginContent(
    modifier: Modifier,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onAuthSuccess: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Bejelentkezés",
            style = Typography.titleLarge.merge(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextFieldContainer(
            modifier = modifier,
            text = state.email,
            label = "Email cím",
            hasError = (state.inputError is InputException.InvalidEmailException || state.submitError != null),
            onValueChange = { value ->
                onAction(LoginAction.ChangeValue(state.copy(
                    email = value
                )))
            },
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
        )
        TextFieldContainer(
            modifier = modifier,
            text = state.password,
            label = "Jelszó",
            hasError = (state.inputError is InputException.InvalidPasswordException || state.submitError != null),
            onValueChange = { value ->
                onAction(LoginAction.ChangeValue(state.copy(
                    password = value
                )))
            },
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            isPasswordField = true,
            isLastAction = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        RememberMe(
            modifier = Modifier,
            state = state,
            onAction = { action ->
                onAction(action)
            }
        )
        ErrorText(
            modifier = Modifier,
            state = state
        )
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(4/5f),
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                enabled = !state.isLoading,
                onClick = {
                    onAction(LoginAction.Submit(onAuthSuccess))
                }
            ){
                Text(
                    text = "Bejelentkezés",
                    style = Typography.bodyLarge
                )
            }
        }
    }
}
@Composable
private fun RememberMe(
    modifier: Modifier,
    state: LoginState,
    onAction: (LoginAction) -> Unit
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Checkbox(
            modifier = modifier.fillMaxHeight(),
            checked = state.rememberUser,
            colors = CheckboxDefaults.colors().copy(
                checkedBoxColor = MaterialTheme.colorScheme.primary,
                checkedCheckmarkColor = MaterialTheme.colorScheme.onPrimary,
                checkedBorderColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedBoxColor = MaterialTheme.colorScheme.surface,
                uncheckedCheckmarkColor = MaterialTheme.colorScheme.primary,
                uncheckedBorderColor = MaterialTheme.colorScheme.primary
            ),
            onCheckedChange = { value ->
                onAction(LoginAction.ChangeValue(
                    state.copy(rememberUser = value)
                ))
            }
        )
        Text(
            text = "Emlékezz rám",
            style = Typography.bodySmall.merge(
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.2.sp
            ),
            maxLines = 1
        )
    }
}
@Composable
private fun ErrorText(
    modifier: Modifier,
    state: LoginState
){
    val text: String? = state.submitError
        ?: if (state.inputError != null) {
            state.inputError.message ?: "Valami hiba történt."
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
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = modifier.height(5.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview(){
    AppTheme(dynamicColor = false) {
        LoginContent(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            state = LoginState(),
            onAction = { _ -> },
            onAuthSuccess = {}
        )
    }
}