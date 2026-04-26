package com.jikokujo.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.repository.UserRepository
import com.jikokujo.core.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface Loadable {
    data class Authentication(val onError: String? = null): Loadable {
        override fun equals(other: Any?): Boolean = other is Authentication
        override fun hashCode(): Int = this::class.hashCode()
    }
    data class User(val onError: String? = null): Loadable {
        override fun equals(other: Any?): Boolean = other is Loadable.User
        override fun hashCode(): Int = this::class.hashCode()
    }
    data class Favourites(val onError: String? = null): Loadable {
        override fun equals(other: Any?): Boolean = other is Favourites
        override fun hashCode(): Int = this::class.hashCode()
    }
}

data class ProfileState(
    val user: User? = null,
    val favourites: List<Favourite>? = null,
    val isLoggedIn: Boolean = false,
    val loading: Set<Loadable> = emptySet(),
    val error: Set<Loadable> = emptySet(),
)
sealed interface ProfileAction{
    data object FetchUser: ProfileAction
    data object AttemptAuth: ProfileAction
    data object LogOut: ProfileAction
    data object FetchFavourites: ProfileAction
    data class ToggleFavourite(val routeId: String, val atMins: Int): ProfileAction
}
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            attemptAuth()
        }
        userRepository.favourites.onEach { favourites ->
            _state.update {
                it.copy(favourites = favourites)
            }
        }.launchIn(viewModelScope)
    }
    suspend fun onAction(action: ProfileAction) = when(action){
        ProfileAction.AttemptAuth -> withContext(ioDispatcher) { attemptAuth() }
        ProfileAction.LogOut -> logout()
        ProfileAction.FetchFavourites -> withContext(ioDispatcher) { getFavourites() }
        is ProfileAction.ToggleFavourite -> withContext(ioDispatcher) { userRepository.toggleFavourite(action.routeId, action.atMins) }
        ProfileAction.FetchUser -> withContext(ioDispatcher) { getUser() }
    }
    private suspend fun logout(){
        userRepository.logout()
        _state.update {
            userRepository.favourites.update { null }
            it.copy(
                isLoggedIn = false,
                user = null,
                favourites = null
            )
        }
    }
    private suspend fun attemptAuth(){
        _state.update {
            it.copy(
                loading = it.loading + Loadable.Authentication(),
                error = it.error - Loadable.Authentication()
            )
        }
        if (userRepository.check()) {
            _state.update {
                it.copy(
                    isLoggedIn = true,
                    favourites = listOf(),
                    loading = it.loading - Loadable.Authentication()
                )
            }
        } else {
            _state.update {
                it.copy(
                    isLoggedIn = false,
                    loading = it.loading - Loadable.Authentication(),
                    error = it.error + Loadable.Authentication("Felhasználó nincs bejelentkezve"),
                )
            }
        }
    }
    private suspend fun getFavourites(){
        _state.update {
            it.copy(
                loading = it.loading + Loadable.Favourites(),
                error = it.error - Loadable.Favourites()
            )
        }
        when (val result = userRepository.getFavourites()) {
            is ApiResult.Error -> _state.update {
                it.copy(
                    loading = it.loading - Loadable.Favourites(),
                    error = it.error + Loadable.Favourites(result.errorMsg)
                )
            }
            is ApiResult.Success -> _state.update {
                it.copy(
                    favourites = result.data,
                    loading = it.loading - Loadable.Favourites()
                )
            }
        }
    }
    private suspend fun getUser(){
        _state.update {
            it.copy(
                loading = it.loading + Loadable.User(),
                error = it.error - Loadable.User()
            )
        }
        when (val result = userRepository.getUser()) {
            is ApiResult.Error -> _state.update {
                it.copy(
                    loading = it.loading - Loadable.User(),
                    error = it.error + Loadable.User(result.errorMsg)
                )
            }
            is ApiResult.Success -> _state.update {
                it.copy(
                    user = result.data,
                    loading = it.loading - Loadable.User()
                )
            }
        }
    }
}