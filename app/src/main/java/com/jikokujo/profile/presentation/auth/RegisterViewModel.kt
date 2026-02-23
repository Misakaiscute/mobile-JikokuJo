package com.jikokujo.profile.presentation.auth

import androidx.lifecycle.ViewModel
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.profile.utils.validateEmail
import com.jikokujo.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val successfulRegistration: Boolean = false,
    val inputError: InputException? = null,
    val submitError: String? = null,
    val isLoading: Boolean = false
)

sealed interface RegisterAction{
    data class ChangeValue(val newState: RegisterState): RegisterAction
    data object Submit: RegisterAction
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    suspend fun onAction(action: RegisterAction) = when(action){
        is RegisterAction.ChangeValue -> changeValue(action.newState)
        is RegisterAction.Submit -> withContext(Dispatchers.IO) { submit() }
    }

    private fun changeValue(newState: RegisterState) = _state.update {
        it.copy(
            firstName = newState.firstName,
            lastName = newState.lastName,
            email = newState.email,
            password = newState.password,
            passwordConfirmation = newState.passwordConfirmation
        )
    }
    @Throws(InputException::class)
    private fun validateInputs() {
        if (
            _state.value.password.isBlank() ||
            _state.value.passwordConfirmation.isBlank() ||
            _state.value.email.isBlank() ||
            _state.value.lastName.isBlank() ||
            _state.value.firstName.isBlank()
        ){
            throw InputException.MissingFieldException("Töltse ki az összes mezőt!")
        } else if (validateEmail(_state.value.email)){
            throw InputException.InvalidEmailException("Adjon meg valós email címet!")
        } else if (_state.value.password.count() < 8){
            throw InputException.InvalidPasswordException("A jelszónak legalább 8 karakter hosszúnak kell lennie!")
        } else if (_state.value.password != _state.value.passwordConfirmation){
            throw InputException.PasswordsNotMatchingException("A jelszavak nem egyeznek!")
        }
    }
    private suspend fun submit() {
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
        val requestResult = userRepository.register(
            firstName = _state.value.firstName,
            lastName = _state.value.lastName,
            email = _state.value.email,
            password = _state.value.password,
            passwordConfirmation = _state.value.passwordConfirmation
        )
        when(requestResult){
            is ApiResult.Error -> _state.update {
                it.copy(
                    submitError = requestResult.errorMsg,
                    isLoading = false
                )
            }
            is ApiResult.Success -> _state.update {
                RegisterState(successfulRegistration = true)
            }
        }
    }
}