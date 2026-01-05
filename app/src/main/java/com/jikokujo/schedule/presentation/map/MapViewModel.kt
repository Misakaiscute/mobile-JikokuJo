package com.jikokujo.schedule.presentation.map

import androidx.lifecycle.ViewModel
import com.jikokujo.schedule.data.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mapsforge.core.model.LatLong

data class MapState(
    val zoomLevel: Byte = 15,
    val center: LatLong = LatLong(47.4933, 19.0533),
    val rotation: Float = 0f,
    val markers: List<Location> = listOf()
)

sealed interface Action{
    data class ChangeZoomLevel(val zoomLevel: Byte): Action
    data class Rotate(val rotation: Float): Action
    data class Move(val location: Location.Anonymous): Action
}

class MapViewModel: ViewModel() {
    private val _state = MutableStateFlow<MapState>(MapState())
    val state = _state.asStateFlow()

    fun onAction(action: Action) = when(action){
        is Action.ChangeZoomLevel -> changeZoomLevel(action.zoomLevel)
        is Action.Move -> move(action.location)
        is Action.Rotate -> rotate(action.rotation)
    }
    private fun changeZoomLevel(zoomLevel: Byte){
        if(zoomLevel in 6..25){
            _state.update {
                it.copy(
                    zoomLevel = zoomLevel
                )
            }
        }
    }
    private fun move(location: Location.Anonymous){
        _state.update {
            it.copy(
                center = LatLong(location.lat, location.lon)
            )
        }
    }
    private fun rotate(rotation: Float){
        _state.update {
            it.copy(
                rotation = rotation
            )
        }
    }
}