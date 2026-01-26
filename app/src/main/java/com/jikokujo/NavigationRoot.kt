package com.jikokujo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jikokujo.schedule.presentation.SchedulePage
import com.jikokujo.profile.presentation.ProfilePage

sealed interface Page : NavKey{
    data object Schedule : Page
    data object Profile : Page
}

@Composable
fun NavigationRoot(
    backstack: SnapshotStateList<Page>,
    modifier: Modifier
){
    NavDisplay(
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Page.Schedule> {
                SchedulePage(modifier)
            }
            entry<Page.Profile> {
                ProfilePage(modifier)
            }
        },
        //TODO: TRANSITION SPEC TO ADD
    )
}