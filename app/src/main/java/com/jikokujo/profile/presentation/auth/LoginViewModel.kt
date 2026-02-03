package com.jikokujo.profile.presentation.auth

import androidx.lifecycle.ViewModel
import com.jikokujo.profile.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
)

class LoginViewModel @Inject constructor(
    userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()
}