package com.jikokujo.profile.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jikokujo.profile.presentation.ProfileState
import com.jikokujo.profile.presentation.Action as ProfileAction

@Composable
fun AuthPage(
    modifier: Modifier,
    state: ProfileState,
    onAuthSuccess: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Register(modifier = modifier)
        HorizontalDivider(
            modifier = modifier,
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        Login(
            modifier = modifier,
            onAuthSuccess = onAuthSuccess
        )
    }
}