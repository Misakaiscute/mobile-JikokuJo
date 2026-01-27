package com.jikokujo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jikokujo.schedule.presentation.map.MapViewModel
import com.jikokujo.schedule.presentation.map.MapsforgeMap
import com.jikokujo.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme(dynamicColor = false) {
                val backstack = remember { mutableStateListOf<Page>(Page.Schedule) }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Image(
                                    modifier = Modifier.fillMaxWidth(),
                                    painter = painterResource(R.drawable.jikokujo),
                                    alignment = Alignment.Center,
                                    contentDescription = "app logo",
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors().copy(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            modifier = Modifier
                                .fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(
                                    shape = RoundedCornerShape(0),
                                    onClick = { backstack.add(Page.Schedule) }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.schedule_icon),
                                        contentDescription = "transportation navigation button",
                                        tint = if (backstack.last() == Page.Schedule) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(
                                    shape = RoundedCornerShape(0),
                                    onClick = { backstack.add(Page.Profile) }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.profile_icon),
                                        contentDescription = "profile navigation button",
                                        tint = if (backstack.last() == Page.Profile) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                ) { innerPadding ->
                    NavigationRoot(
                        backstack = backstack,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}