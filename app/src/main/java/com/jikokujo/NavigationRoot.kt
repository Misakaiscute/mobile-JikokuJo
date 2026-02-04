package com.jikokujo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jikokujo.schedule.presentation.SchedulePage
import com.jikokujo.profile.presentation.ProfileNavRoot

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
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MainPage.Schedule> {
                SchedulePage(modifier)
            }
            entry<MainPage.Profile> {
                ProfileNavRoot(modifier)
            }
        },
        //TODO: TRANSITION SPEC TO ADD
    )
}