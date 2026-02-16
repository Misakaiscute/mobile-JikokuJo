package com.jikokujo.profile.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.profile.presentation.ProfileAction
import com.jikokujo.profile.presentation.ProfileState
import com.jikokujo.theme.Typography

@Composable
fun ProfilePage(
    modifier: Modifier,
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
){
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.user!!.firstName + ' ' + state.user.lastName,
            style = Typography.labelMedium.merge(
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        )
        Row(
            modifier = modifier.height(120.dp)
        ) {
            CustomPanelButton(
                modifier = Modifier.fillMaxHeight(),
                icon = R.drawable.favourites_star,
                text = "Adataim",
                isEnabled = !state.isLoading,
                onClick = {}
            )
            CustomPanelButton(
                modifier = Modifier.fillMaxHeight(),
                icon = R.drawable.favourites_star,
                text = "Beállítások",
                isEnabled = !state.isLoading,
                onClick = {}
            )
        }
        CustomPanelButton(
            modifier = Modifier.height(120.dp),
            icon = R.drawable.favourites_star,
            text = "Kedvencek",
            isEnabled = !state.isLoading,
            onClick = {}
        )
        Row(
            modifier = modifier.height(120.dp)
        ) {
            CustomPanelButton(
                modifier = Modifier.fillMaxHeight(),
                icon = R.drawable.favourites_star,
                text = "ÁSZF ❐",
                isEnabled = !state.isLoading,
                onClick = {}
            )
            CustomPanelButton(
                modifier = Modifier.fillMaxHeight(),
                icon = R.drawable.favourites_star,
                text = "Kijelentkezés",
                isEnabled = !state.isLoading,
                onClick = { onAction(ProfileAction.LogOut) }
            )
        }
    }
}