package com.jikokujo.profile.presentation.auth

import androidx.lifecycle.ViewModel
import com.jikokujo.core.utils.validateEmail
import com.jikokujo.profile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val rememberUser: Boolean = false,
    val inputError: InputException? = null,
    val submitError: String? = null,
    val isLoading: Boolean = false
)

sealed interface LoginAction{
    data class ChangeValue(val newState: LoginState): LoginAction
    data class Submit(val onSuccess: () -> Unit): LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    suspend fun onAction(action: LoginAction) = when(action){
        is LoginAction.ChangeValue -> changeValue(action.newState)
        is LoginAction.Submit -> withContext(Dispatchers.IO) { submit(action.onSuccess) }
    }

    private fun changeValue(newState: LoginState) = _state.update {
        it.copy(
            email = newState.email,
            password = newState.password,
            rememberUser = newState.rememberUser
        )
    }
    @Throws(InputException::class)
    private fun validateInputs() {
        if (_state.value.password.isBlank() || _state.value.email.isBlank()) {
            throw InputException.MissingFieldException("Töltse ki az összes mezőt!")
        } else if (validateEmail(_state.value.email)) {
            throw InputException.InvalidEmailException("Adjon meg valós email címet!")
        } else if (_state.value.password.count() < 8) {
            throw InputException.InvalidPasswordException("A jelszónak legalább 8 karakter hosszúnak kell lennie!")
        }
    }
    private suspend fun submit(onSuccess: () -> Unit){
        _state.update {
            it.copy(
                inputError = null,
                submitError = null,
                isLoading = true,
            )
        }
        try {
            validateInputs()
        } catch (e: InputException) {
            _state.update {
                it.copy(
                    inputError = e,
                    isLoading = false
                )
            }
            return
        }
        userRepository.login(
            email = _state.value.email,
            password = _state.value.password,
            remember = _state.value.rememberUser
        )
        if (userRepository.userAccessToken != null){
            _state.update {
                LoginState()
            }
            onSuccess()
        } else {
            _state.update {
                it.copy(
                    submitError = "Hibás email cím vagy jelszó.",
                    isLoading = false
                )
            }
        }
    }
}