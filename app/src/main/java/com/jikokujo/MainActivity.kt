package com.jikokujo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
                val backstack = rememberSaveable { mutableStateListOf<MainPage>(MainPage.Schedule) }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                TitleBar(
                                    modifier = Modifier,
                                    onBackPressed = {
                                        onBackPressedDispatcher.onBackPressed()
                                    },
                                    onNavigate = {
                                        when (backstack.last()){
                                            is MainPage.Profile -> backstack.add(MainPage.Schedule)
                                            else -> backstack.add(MainPage.Profile)
                                        }
                                    },
                                    currentPage = backstack.last()
                                )
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
@Composable
private fun TitleBar(
    modifier: Modifier,
    onBackPressed: () -> Unit,
    onNavigate: () -> Unit,
    currentPage: MainPage
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = modifier,
            onClick = onBackPressed,
            shape = RoundedCornerShape(size = 0.dp),
        ) {
            Icon(
                modifier = modifier,
                painter = painterResource(R.drawable.navigate_back),
                contentDescription = "back button",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            modifier = modifier,
            painter = painterResource(R.drawable.jikokujo),
            contentDescription = "app logo",
            tint = MaterialTheme.colorScheme.onSurface
        )
        IconButton(
            modifier = modifier,
            onClick = onNavigate,
            shape = RoundedCornerShape(size = 0.dp)
        ) {
            val navigationIcon = painterResource(when (currentPage){
                is MainPage.Profile -> R.drawable.schedule_icon
                else -> R.drawable.profile
            })
            Icon(
                modifier = modifier,
                painter = navigationIcon,
                contentDescription = "back button",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}