package com.jikokujo.profile.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
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
    val isUserLoggedIn: Boolean = false,
    val backStack: List<ProfilePage>? = null,
    val isLoading: Boolean = false,
)
sealed interface ProfileAction{
    data object SuccessfulAuth: ProfileAction
    data class Navigate(val page: ProfilePage): ProfileAction
    data object NavigateBack: ProfileAction
}
@HiltViewModel
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
                    backStack = mutableStateListOf(ProfilePage.Main),
                    isLoading = false
                )
            }
        }
    }
    suspend fun onAction(action: ProfileAction) = when(action){
        is ProfileAction.SuccessfulAuth -> onSuccessfulAuth()
        is ProfileAction.Navigate -> navigate(action.page)
        is ProfileAction.NavigateBack -> navigate(null)
    }
    private fun onSuccessfulAuth(){
        if (userRepository.userAccessToken != null){
            _state.update {
                it.copy(
                    isUserLoggedIn = true,
                    backStack = mutableStateListOf(ProfilePage.Main)
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