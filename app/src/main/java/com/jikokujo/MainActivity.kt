package com.jikokujo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.jikokujo.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val backstack = remember { mutableStateListOf<MainPage>(MainPage.Schedule) }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                IconButton(
                                    onClick = { onBackPressedDispatcher.onBackPressed() },
                                    shape = RoundedCornerShape(size = 0.dp),
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        painter = painterResource(R.drawable.navigate_back),
                                        contentDescription = "back button",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            title = {
                                Icon(
                                    modifier = Modifier.fillMaxWidth(),
                                    painter = painterResource(R.drawable.jikokujo),
                                    contentDescription = "app logo",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        when (backstack.last()){
                                            is MainPage.Profile -> backstack.add(MainPage.Schedule)
                                            else -> backstack.add(MainPage.Profile)
                                        }
                                    },
                                    shape = RoundedCornerShape(size = 0.dp)
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        painter = painterResource(when (backstack.last()){
                                            is MainPage.Profile -> R.drawable.schedule_icon
                                            else -> R.drawable.profile
                                        }),
                                        contentDescription = "back button",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors().copy(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                        )
                    }
                ) { innerPadding ->
                    NavigationRoot(
                        backstack = backstack,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}