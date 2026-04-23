package com.jikokujo

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jikokujo.schedule.presentation.SchedulePage
import com.jikokujo.profile.presentation.ProfilePage

sealed interface MainPage : NavKey{
    data object Schedule : MainPage
    data object Profile : MainPage
}

@Composable
fun NavigationRoot(
    backstack: SnapshotStateList<MainPage>,
    modifier: Modifier
){
    NavDisplay(
        modifier = modifier,
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MainPage.Schedule> {
                SchedulePage(Modifier)
            }
            entry<MainPage.Profile> {
                ProfilePage(Modifier)
            }
        },
        transitionSpec = {
            // Slide in from right when navigating forward
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
    )
}