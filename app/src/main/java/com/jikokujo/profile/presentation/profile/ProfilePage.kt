package com.jikokujo.profile.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.user!!.firstName + ' ' + state.user.lastName,
            style = Typography.titleLarge.merge(
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        )
        Row(
            modifier = modifier
                .height(120.dp)
                .fillMaxWidth()
        ) {
            CustomPanelButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = R.drawable.my_data,
                color = MaterialTheme.colorScheme.primary,
                text = "Adataim",
                isEnabled = !state.isLoading,
                onClick = {}
            )
            CustomPanelButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = R.drawable.settings,
                color = MaterialTheme.colorScheme.primary,
                text = "Beállítások",
                isEnabled = !state.isLoading,
                onClick = {}
            )
        }
        CustomPanelButton(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            icon = R.drawable.favourites_star,
            color = MaterialTheme.colorScheme.primary,
            text = "Kedvencek",
            isEnabled = !state.isLoading,
            onClick = {}
        )
        Row(
            modifier = modifier
                .height(120.dp)
                .fillMaxWidth()
        ) {
            CustomPanelButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = R.drawable.privacy_policy,
                color = MaterialTheme.colorScheme.primary,
                text = "ÁSZF ❐",
                isEnabled = !state.isLoading,
                onClick = {}
            )
            CustomPanelButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = R.drawable.logout,
                color = MaterialTheme.colorScheme.error,
                text = "Kijelentkezés",
                isEnabled = !state.isLoading,
                onClick = { onAction(ProfileAction.LogOut) }
            )
        }
    }
}