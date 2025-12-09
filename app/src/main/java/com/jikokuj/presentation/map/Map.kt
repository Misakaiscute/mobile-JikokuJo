package com.jikokuj.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun DisplayMapsforgeMap(modifier: Modifier){
    Box(
        modifier = modifier.fillMaxSize()
    ) {

    }
}
@Composable
fun MapsforgeMap(modifier: Modifier){
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AndroidGraphic
        },
        update = {

        }
    )
}