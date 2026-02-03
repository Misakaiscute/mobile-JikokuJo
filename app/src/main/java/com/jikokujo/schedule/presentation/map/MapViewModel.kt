package com.jikokujo.schedule.presentation.map

import androidx.lifecycle.ViewModel
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.core.data.ApiResult
import com.jikokujo.schedule.data.repository.TripsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

data class MapState(
    val zoomLevel: Byte = 15,
    val rotation: Float = 0f,
    val routeAssociated: Queryable.Route? = null,
    val pathPoints: List<Location.RoutePathPoint> = listOf(),
    val stops: List<StopWithLocationAndStopTime> = listOf(),
    val error: String? = null
)

sealed interface Action{
    data class ChangeZoomLevel(val zoomIn: Boolean): Action
    data class Rotate(val rotation: Float): Action
    data class SelectTrip(val trip: Trip, val routeAssociated: Queryable.Route): Action
    data object UnselectTrip: Action
}

@HiltViewModel
class MapViewModel @Inject constructor(
    private val tripsRepository: TripsRepository
): ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    fun onAction(action: Action) = when(action){
        is Action.ChangeZoomLevel -> changeZoomLevel(action.zoomIn)
        is Action.Rotate -> rotate(action.rotation)
        is Action.SelectTrip -> runBlocking(Dispatchers.IO) { selectTrip(action.trip, action.routeAssociated) }
        is Action.UnselectTrip -> runBlocking(Dispatchers.IO) { selectTrip() }
    }
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    private suspend fun selectTrip(trip: Trip? = null, routeAssociated: Queryable.Route? = null) {
        if (trip == null){
            _state.update {
                it.copy(
                    routeAssociated = null,
                    pathPoints = listOf(),
                    stops = listOf(),
                    error = null
                )
            }
        } else {
            if (tripsRepository.trips is ApiResult.Error){
                throw IllegalStateException("Fetched trips can't be ApiResult.Error, if a trip was selected")
            } else if ((tripsRepository.trips as ApiResult.Success).data.find { t -> t.id == trip.id} == null) {
                throw IllegalArgumentException("Can't select a trip that's not in the dataset")
            }
            if (!tripsRepository.storedShapes.containsKey(trip.shapeId)){
                tripsRepository.getShapes(trip)
            }
            if (!tripsRepository.storedStops.containsKey(trip.shapeId)){
                tripsRepository.getStops(trip)
            }

            if (tripsRepository.storedShapes[trip.shapeId] is ApiResult.Success && tripsRepository.storedStops[trip.id] is ApiResult.Success){
                _state.update {
                    it.copy(
                        routeAssociated = routeAssociated,
                        pathPoints = (tripsRepository.storedShapes[trip.shapeId] as ApiResult.Success).data,
                        stops = (tripsRepository.storedStops[trip.id] as ApiResult.Success).data,
                        error = null
                    )
                }
            } else {
                _state.update {
                    if (tripsRepository.storedShapes[trip.shapeId] is ApiResult.Error){
                        it.copy(
                            routeAssociated = null,
                            pathPoints = listOf(),
                            stops = listOf(),
                            error = "${(tripsRepository.storedShapes[trip.shapeId] as ApiResult.Error).errorMsg} Retry?"
                        )
                    } else {
                        it.copy(
                            routeAssociated = null,
                            pathPoints = listOf(),
                            stops = listOf(),
                            error = "${(tripsRepository.storedStops[trip.id] as ApiResult.Error).errorMsg} Retry?"
                        )
                    }
                }
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
    private fun rotate(rotation: Float) = _state.update {
        it.copy(
            rotation = rotation
        )
    }
}