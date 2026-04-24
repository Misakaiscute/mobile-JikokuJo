package com.jikokujo.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.core.data.repository.UserRepository
import com.jikokujo.core.di.IoDispatcher
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.RoutePathPoint
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.repository.TripsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface Loadable {
    data class Trip(val onError: String? = null): Loadable {
        override fun equals(other: Any?): Boolean = other is Trip
        override fun hashCode(): Int = this::class.hashCode()
    }
    data class Favourites(val onError: String? = null): Loadable {
        override fun equals(other: Any?): Boolean = other is Favourites
        override fun hashCode(): Int = this::class.hashCode()
    }
}
data class TripInfoState(
    val trip: Trip? = null,
    val selectedThrough: Queryable? = null,
    val routeAssociated: Queryable.Route? = null,
    val pathPoints: List<RoutePathPoint> = listOf(),
    val stops: List<StopWithLocationAndStopTime> = listOf(),
    val favourites: List<Favourite>? = null,
    val tripInfoShown: Boolean = false,
    val loading: Set<Loadable> = emptySet(),
    val error: Set<Loadable> = emptySet()
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
    data class ToggleFavourite(val routeId: String, val atMins: Int): TripAction
}

@HiltViewModel
class TripInfoViewModel @Inject constructor(
    private val tripsRepository: TripsRepository,
    private val userRepository: UserRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {
    private val _state = MutableStateFlow(TripInfoState())
    val state = _state.asStateFlow()

    init {
        userRepository.favourites.onEach { favourites ->
            _state.update {
                it.copy(favourites = favourites)
            }
        }.launchIn(viewModelScope)
    }

    suspend fun onAction(action: TripAction) = when(action){
        is TripAction.SelectTrip -> withContext(ioDispatcher) {
            selectTrip(action.trip, action.routeAssociated, action.selectedThrough)
        }
        is TripAction.UnselectTrip -> unselectTrip()
        TripAction.HideTripInfo -> _state.update {
            it.copy(tripInfoShown = false)
        }
        TripAction.ShowTripInfo -> _state.update {
            it.copy(tripInfoShown = true)
        }
        is TripAction.ToggleFavourite -> withContext(ioDispatcher) {
            userRepository.toggleFavourite(action.routeId, action.atMins)
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
            it.copy(
                loading = it.loading + Loadable.Trip(),
                error = it.error - Loadable.Trip()
            )
        }

        val shapes: List<RoutePathPoint> = tripsRepository.getShapes(trip)
        val stops: List<StopWithLocationAndStopTime> = tripsRepository.getStops(trip)

        if (shapes.count() > 0 && stops.count() > 0){
            successfulTripSelect(
                trip = trip,
                routeAssociated = routeAssociated,
                selectedThrough = selectedThrough
            )
            getFavourites()
        } else {
            failedTripSelect()
        }
    }
    private suspend fun successfulTripSelect(
        trip: Trip,
        routeAssociated: Queryable.Route,
        selectedThrough: Queryable
    ){
        val shapes: List<RoutePathPoint> = tripsRepository.getShapes(trip)
        val stops: List<StopWithLocationAndStopTime> = tripsRepository.getStops(trip)

        _state.update {
            it.copy(
                trip = trip,
                selectedThrough = selectedThrough,
                routeAssociated = routeAssociated,
                pathPoints = shapes,
                stops = stops,
                tripInfoShown = true,
                loading = it.loading - Loadable.Trip(),
            )
        }
    }
    private fun failedTripSelect(){
        _state.update {
            it.copy(
                trip = null,
                routeAssociated = null,
                pathPoints = listOf(),
                stops = listOf(),
                tripInfoShown = false,
                loading = it.loading - Loadable.Trip(),
                error = it.error + Loadable.Trip("Járat útjának betöltése sikertelen volt.")
            )
        }
    }
    private fun unselectTrip() {
        _state.update {
            it.copy(
                trip = null,
                routeAssociated = null,
                pathPoints = listOf(),
                stops = listOf(),
                error = it.error - Loadable.Trip()
            )
        }
    }
    private suspend fun getFavourites(){
        _state.update {
            it.copy(
                loading = it.loading + Loadable.Favourites(),
                error = it.error - Loadable.Favourites()
            )
        }
        when (val result = userRepository.getFavourites()){
            is ApiResult.Success -> {
                _state.update {
                    it.copy(loading = it.loading - Loadable.Favourites())
                }
            }
            is ApiResult.Error -> {
                _state.update {
                    it.copy(
                        loading = it.loading - Loadable.Favourites(),
                        error = it.error + Loadable.Trip(result.errorMsg)
                    )
                }
            }
        }
    }
}