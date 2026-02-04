package com.jikokujo.profile.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jikokujo.profile.presentation.ProfileState

@Composable
fun AuthPage(
    modifier: Modifier,
    onAuthSuccess: () -> Unit
) {
    Spacer(modifier = modifier.height(10.dp))
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Register(
            modifier = modifier
        )
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
    Spacer(modifier = modifier.height(10.dp))
}