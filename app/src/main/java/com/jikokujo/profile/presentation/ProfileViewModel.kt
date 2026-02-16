package com.jikokujo.profile.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.jikokujo.core.data.ApiResult
import com.jikokujo.profile.data.model.User
import com.jikokujo.profile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfilePage: NavKey {
    data object Main: ProfilePage
    data object Favourites: ProfilePage
}

data class ProfileState(
    val user: User? = null,
    val backStack: List<ProfilePage>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
sealed interface ProfileAction{
    data object AttemptAuth: ProfileAction
    data class Navigate(val page: ProfilePage): ProfileAction
    data object NavigateBack: ProfileAction
    data object LogOut: ProfileAction
}
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default){
            attemptAuth()
        }
    }
    suspend fun onAction(action: ProfileAction) = when(action){
        is ProfileAction.AttemptAuth -> attemptAuth()
        is ProfileAction.Navigate -> navigate(action.page)
        is ProfileAction.NavigateBack -> navigate(null)
        is ProfileAction.LogOut -> logout()
    }
    private suspend fun logout(){
        userRepository.logout()
        _state.update {
            it.copy(
                user = null,
                backStack = null
            )
        }
    }
    private suspend fun attemptAuth(){
        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
        userRepository.getLoggedInUser()
        when (userRepository.loggedInUser){
            is ApiResult.Success -> _state.update {
                it.copy(
                    user = (userRepository.loggedInUser as ApiResult.Success).data,
                    backStack = listOf(ProfilePage.Main),
                    isLoading = false,
                    error = null
                )
            }
            is ApiResult.Error -> _state.update {
                it.copy(
                    user = null,
                    backStack = null,
                    isLoading = false,
                    error = (userRepository.loggedInUser as ApiResult.Error).errorMsg
                )
            }
            else -> _state.update {
                it.copy(
                    user = null,
                    backStack = null,
                    isLoading = false,
                )
            }
        }
    }
    @Throws(IllegalStateException::class)
    private fun navigate(toPage: ProfilePage?){
        if (_state.value.backStack == null){
            throw IllegalStateException("Backstack must be initialized for navigation")
        }
        if (toPage == null){
            val newBackStack = _state.value.backStack!!.toMutableList()
            newBackStack.removeLastOrNull()

            _state.update {
                it.copy(backStack = newBackStack)
            }
        } else {
            val newBackStack = _state.value.backStack!!.toMutableList()
            newBackStack.add(toPage)

            _state.update {
                it.copy(backStack = newBackStack)
            }
        }
    }
}