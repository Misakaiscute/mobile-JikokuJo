package com.jikokujo.profile.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jikokujo.R
import com.jikokujo.profile.presentation.ProfileAction
import com.jikokujo.profile.presentation.ProfileState
import com.jikokujo.theme.AppTheme
import com.jikokujo.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun ProfilePage(
    modifier: Modifier,
    state: ProfileState,
    onAction: (ProfileAction) -> Unit
){
    LaunchedEffect(true) {
        launch {
            onAction(ProfileAction.FetchUser)
        }
        launch {
            onAction(ProfileAction.FetchFavourites)
        }
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(10.dp))
        SectionHeading(Modifier, "Adataim", R.drawable.my_data)
        UserAccountContent(
            modifier = Modifier,
            profileState = state,
        )
        Spacer(Modifier.height(10.dp))
        SectionHeading(Modifier, "Kedvencek", R.drawable.favourites_star)
        FavouritesContent(
            modifier = Modifier,
            profileState = state,
            onAction = { action ->
                onAction(action)
            }
        )
        Spacer(Modifier.weight(1f))
        Logout(
            modifier = Modifier,
            onClick = {
                onAction(ProfileAction.LogOut)
            }
        )
        Spacer(Modifier.height(20.dp))
    }
}
@Composable
private fun Logout(
    modifier: Modifier,
    onClick: () -> Unit
){
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier
                .padding(end = 20.dp)
                .clickable(
                    enabled = true,
                    onClick = onClick
                ),
            text = "Kijelentkezés",
            style = Typography.bodyMedium.merge(
                color = MaterialTheme.colorScheme.error,
                textDecoration = TextDecoration.Underline
            )
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun ProfilePagePreview(){
    AppTheme(dynamicColor = false) {
        ProfilePage(
            modifier = Modifier,
            state = ProfileState(),
            onAction = { _ ->  }
        )
    }
}