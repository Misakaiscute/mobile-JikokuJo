package com.jikokujo.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.profile.data.model.User
import com.jikokujo.profile.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isUserLoggedIn: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface Action{
    data object SuccessfulAuth: Action
}

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default){
            _state.update {
                it.copy(isLoading = true)
            }
            userRepository.checkAuth()
            _state.update {
                it.copy(
                    isUserLoggedIn = userRepository.userAccessToken != null,
                    isLoading = false
                )
            }
        }
    }

    fun onAction(action: Action) = when(action){
        is Action.SuccessfulAuth -> onSuccessfulAuth()
    }

    private fun onSuccessfulAuth(){
        if (userRepository.userAccessToken != null){
            _state.update {
                it.copy(isUserLoggedIn = true)
            }
        }
    }
}