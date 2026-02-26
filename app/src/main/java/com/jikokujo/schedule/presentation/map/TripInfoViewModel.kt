package com.jikokujo.schedule.presentation.map

import androidx.lifecycle.ViewModel
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.model.RoutePathPoint
import com.jikokujo.schedule.data.repository.TripsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class TripInfoState(
    val shapeId: String? = null,
    val selectedThrough: Queryable? = null,
    val routeAssociated: Queryable.Route? = null,
    val pathPoints: List<RoutePathPoint> = listOf(),
    val stops: List<StopWithLocationAndStopTime> = listOf(),
    val tripInfoShown: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface TripAction{
    data class SelectTrip(
        val trip: Trip,
        val routeAssociated: Queryable.Route,
        val selectedThrough: Queryable
    ): TripAction
    data object UnselectTrip: TripAction
    data object ShowTripInfo: TripAction
    data object HideTripInfo: TripAction
}

@HiltViewModel
class TripInfoViewModel @Inject constructor(
    private val tripsRepository: TripsRepository
): ViewModel() {
    private val _state = MutableStateFlow(TripInfoState())
    val state = _state.asStateFlow()

    suspend fun onAction(action: TripAction) = when(action){
        is TripAction.SelectTrip -> withContext(Dispatchers.IO) {
            selectTrip(action.trip, action.routeAssociated, action.selectedThrough)
        }
        is TripAction.UnselectTrip -> unselectTrip()
        TripAction.HideTripInfo -> _state.update {
            it.copy(tripInfoShown = false)
        }
        TripAction.ShowTripInfo -> _state.update {
            it.copy(tripInfoShown = true)
        }
    }

    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    private suspend fun selectTrip(
        trip: Trip,
        routeAssociated: Queryable.Route,
        selectedThrough: Queryable
    ) {
        if (tripsRepository.trips is ApiResult.Error){
            throw IllegalStateException("Fetched trips can't be ApiResult.Error, if a trip was selected")
        } else if ((tripsRepository.trips as ApiResult.Success).data.find { t -> t.id == trip.id } == null){
            throw IllegalArgumentException("Can't select a trip that's not in the dataset")
        }

        _state.update {
            it.copy(isLoading = true)
        }

        tripsRepository.getShapes(trip)
        tripsRepository.getStops(trip)

        if (tripsRepository.storedShapes[trip.shapeId] is ApiResult.Success && tripsRepository.storedStops[trip.id] is ApiResult.Success){
            successfulTripSelect(
                trip = trip,
                routeAssociated = routeAssociated,
                selectedThrough = selectedThrough
            )
        } else {
            failedTripSelect(trip)
        }
    }
    private fun successfulTripSelect(
        trip: Trip,
        routeAssociated: Queryable.Route,
        selectedThrough: Queryable
    ){
        _state.update {
            it.copy(
                shapeId = trip.shapeId,
                selectedThrough = selectedThrough,
                routeAssociated = routeAssociated,
                pathPoints = (tripsRepository.storedShapes[trip.shapeId] as ApiResult.Success).data,
                stops = (tripsRepository.storedStops[trip.id] as ApiResult.Success).data,
                tripInfoShown = true,
                isLoading = false,
                error = null
            )
        }
    }
    private fun failedTripSelect(trip: Trip){
        _state.update {
            if (tripsRepository.storedShapes[trip.shapeId] is ApiResult.Error){
                it.copy(
                    shapeId = null,
                    routeAssociated = null,
                    pathPoints = listOf(),
                    stops = listOf(),
                    tripInfoShown = false,
                    isLoading = false,
                    error = "${(tripsRepository.storedShapes[trip.shapeId] as ApiResult.Error).errorMsg} Retry?",
                )
            } else {
                it.copy(
                    shapeId = null,
                    routeAssociated = null,
                    pathPoints = listOf(),
                    stops = listOf(),
                    tripInfoShown = false,
                    isLoading = false,
                    error = "${(tripsRepository.storedStops[trip.id] as ApiResult.Error).errorMsg} Retry?"
                )
            }
        }
    }
    private fun unselectTrip() {
        _state.update {
            it.copy(
                shapeId = null,
                routeAssociated = null,
                pathPoints = listOf(),
                stops = listOf(),
                error = null
            )
        }
    }
}