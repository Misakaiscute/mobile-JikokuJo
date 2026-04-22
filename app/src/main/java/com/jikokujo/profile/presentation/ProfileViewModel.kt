package com.jikokujo.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface Loadable {
    data class Authentication(val onError: String? = null): Loadable {
        override fun equals(other: Any?): Boolean = other is Authentication
        override fun hashCode(): Int {
            return super.hashCode()
        }
    }
    data class User(val onError: String? = null): Loadable {
        override fun equals(other: Any?): Boolean = other is Loadable.User
        override fun hashCode(): Int {
            return super.hashCode()
        }
    }
    data class Favourites(val onError: String? = null): Loadable {
        override fun equals(other: Any?): Boolean = other is Favourites
        override fun hashCode(): Int {
            return super.hashCode()
        }
    }
}

data class ProfileState(
    val user: User? = null,
    val favourites: List<Favourite>? = null,
    val isLoggedIn: Boolean = false,
    val loading: Set<Loadable> = emptySet(),
    val error: Set<Loadable> = emptySet()
)
sealed interface ProfileAction{
    data object FetchUser: ProfileAction
    data object AttemptAuth: ProfileAction
    data object LogOut: ProfileAction
    data object FetchFavourites: ProfileAction
    data class ToggleFavourite(val favourite: Favourite): ProfileAction
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
        ProfileAction.AttemptAuth -> attemptAuth()
        ProfileAction.LogOut -> logout()
        ProfileAction.FetchFavourites -> getFavourites()
        is ProfileAction.ToggleFavourite -> {}
        ProfileAction.FetchUser -> getUser()
    }
    private suspend fun logout(){
        userRepository.logout()
        _state.update {
            it.copy(isLoggedIn = false)
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
                    error = it.error + Loadable.Authentication("Felhasználó nincs bejelentkezve")
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
    private suspend fun toggleFavourite(forRouteId: String, atMins: Int){
        val result = userRepository.toggleFavourite(forRouteId, atMins)
        if (result is ApiResult.Success){
            if (result.data == null) {
                val filtered: List<Favourite> = _state.value.favourites!!.filterNot {
                    return@filterNot it.route.id == forRouteId && it.atMins == atMins
                }
                _state.update {
                    it.copy(favourites = filtered)
                }
            } else {
                val newFavourite = Favourite(
                    route = result.data,
                    atMins = atMins,
                )
                val alteredFavourites: MutableList<Favourite> = _state.value.favourites!!.toMutableList()
                alteredFavourites.add(newFavourite)
                _state.update {
                    it.copy(favourites = alteredFavourites)
                }
            }
        }
    }
}