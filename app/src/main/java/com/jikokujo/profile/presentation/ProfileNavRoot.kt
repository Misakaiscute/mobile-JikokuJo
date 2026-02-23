package com.jikokujo.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jikokujo.profile.presentation.auth.AuthPage
import com.jikokujo.profile.presentation.profile.ProfilePage
import kotlinx.coroutines.launch

@Composable
fun ProfileNavRoot(modifier: Modifier){
    val profileViewModel = viewModel<ProfileViewModel>()
    val state = profileViewModel.state.collectAsStateWithLifecycle().value

    //TODO: Add some sort of loading indication
    Surface(
        modifier = modifier.padding(horizontal = 10.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.user == null) {
            AuthPage(
                modifier = Modifier,
                onAuthSuccess = {
                    profileViewModel.viewModelScope.launch {
                        profileViewModel.onAction(ProfileAction.AttemptAuth)
                    }
                }
            )
        } else {
            NavDisplay(
                backStack = state.backStack!!,
                onBack = {
                    profileViewModel.viewModelScope.launch {
                        profileViewModel.onAction(ProfileAction.NavigateBack)
                    }
                },
                entryProvider = entryProvider {
                    entry<ProfilePage.Main> {
                        ProfilePage(
                            modifier = Modifier,
                            state = state,
                            onAction = { action ->
                                profileViewModel.viewModelScope.launch {
                                    profileViewModel.onAction(action)
                                }
                            }
                        )
                    }
                    entry<ProfilePage.Favourites> {
                        Box(
                            Modifier.fillMaxSize().background(Color.Blue)
                        ) { }
                    }
                }
            )
        }
    }
}