package com.jikokujo.schedule.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Stop
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.remote.Api
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.repository.RouteResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mapsforge.core.model.LatLong
import javax.inject.Inject

data class MapState(
    val zoomLevel: Byte = 15,
    val center: LatLong = LatLong(47.4933, 19.0533),
    val rotation: Float = 0f,
    val pathPoints: List<Location.RoutePathPoint> = listOf(),
    val stops: List<Stop> = listOf()
)

sealed interface Action{
    data class ChangeZoomLevel(val zoomIn: Boolean): Action
    data class Rotate(val rotation: Float): Action
    data class Move(val location: Location.Auxiliary): Action
    data class SetFetchedNodes(val tripId: String): Action
    data object ResetNodes: Action
}

@HiltViewModel
class MapViewModel @Inject constructor(
    private val routeResultRepository: RouteResultRepository
): ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    fun onAction(action: Action) = when(action){
        is Action.ChangeZoomLevel -> changeZoomLevel(action.zoomIn)
        is Action.Move -> move(action.location)
        is Action.Rotate -> rotate(action.rotation)
        is Action.SetFetchedNodes -> setFetchedNodes(action.tripId)
        is Action.ResetNodes -> setFetchedNodes(null)
    }
    private fun setFetchedNodes(tripId: String?) = _state.update{
        if (tripId == null){
            it.copy(
                pathPoints = listOf(),
                stops = listOf()
            )
        } else {
            if (routeResultRepository.trips is ApiResult.Success && routeResultRepository.possibleShapes is ApiResult.Success){
                it.copy(
                    pathPoints = (routeResultRepository.possibleShapes as ApiResult.Success).data[
                        (routeResultRepository.trips as ApiResult.Success).data.find{ trip ->
                            trip.id == tripId
                        }
                    ?.shape]!!.toList(),
                    stops = (routeResultRepository.trips as ApiResult.Success).data.find{ trip ->
                        trip.id == tripId
                    }!!.stops
                )
            } else {
                it.copy()
            }
        }
    }
    private fun changeZoomLevel(zoomIn: Boolean){
        if(_state.value.zoomLevel in 6..25){
            _state.update {
                it.copy(
                    zoomLevel = if (zoomIn) (_state.value.zoomLevel + 1).toByte() else (_state.value.zoomLevel - 1).toByte()
                )
            }
        }
    }
    private fun move(location: Location.Auxiliary) = _state.update {
        it.copy(
            center = LatLong(location.lat, location.lon)
        )
    }
    private fun rotate(rotation: Float) = _state.update {
        it.copy(
            rotation = rotation
        )
    }
}