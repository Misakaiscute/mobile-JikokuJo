package com.jikokujo.profile.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokujo.profile.presentation.auth.AuthPage

@Composable
fun ProfilePage(modifier: Modifier){
    val profileViewModel = viewModel<ProfileViewModel>()
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        if (profileViewModel.state.collectAsStateWithLifecycle().value.isUserLoggedIn) {
            AuthPage(
                modifier = Modifier,
                state = profileViewModel.state.collectAsStateWithLifecycle().value,
                onAuthSuccess = { profileViewModel.onAction(Action.SuccessfulAuth) }
            )
        } else {
            TODO("Account settings not yet implemented")
        }
    }
}