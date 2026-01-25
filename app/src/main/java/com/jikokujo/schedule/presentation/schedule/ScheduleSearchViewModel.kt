package com.jikokujo.schedule.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.repository.QueryableRepository
import com.jikokujo.schedule.data.repository.TripsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject

enum class Dialogs{
    DatePicker, TimePicker
}
enum class DropDowns{
    QueryableSelection, TripSelection
}

data class ScheduleSearchState(
    val queryables: List<Queryable> = listOf(),
    val searchString: String = "",
    val selectedQueryable: Queryable? = null,
    val tripTimeConstraint: LocalDateTime = LocalDateTime.now(),
    val trips: List<Trip> = listOf(),
    val selectedTrip: Trip? = null,
    val dropDownExpanded: Boolean = false,
    val dropDownShown: DropDowns = DropDowns.QueryableSelection,
    val shownDialog: Dialogs? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface Action{
    data class ChangeDropDownState(val isExpanded: Boolean, val dropDownShown: DropDowns? = null): Action
    data class ShowDialog(val dialog: Dialogs?): Action
    data class ChangeFromDate(val year: Int, val month: Int, val day: Int): Action
    data class ChangeFromTime(val hour: Int, val minute: Int): Action
    data class ChangeSearch(val qString: String): Action
    data class SelectRoute(val route: Queryable.Route): Action
    data class SelectStop(val stop: Queryable.Stop): Action
    data class SelectTrip(val trip: Trip): Action
    data class GetRoute(val routeId: String): Action
    data object UnselectQueryable: Action
    data object UnselectTrip: Action
    data object Search: Action
}
@HiltViewModel
class ScheduleSearchViewModel @Inject constructor(
    private val queryableRepository: QueryableRepository,
    private val tripsRepository: TripsRepository
): ViewModel() {
    private val _state = MutableStateFlow(ScheduleSearchState())
    val state = _state.asStateFlow()

    init {
        toggleLoading()
        viewModelScope.launch(Dispatchers.IO) {
            queryableRepository.getQueryables()
            _state.update {
                when (queryableRepository.queryables) {
                    is ApiResult.Error -> {
                        it.copy(
                            error = (queryableRepository.queryables as ApiResult.Error).errorMsg,
                            isLoading = false
                        )
                    }
                    is ApiResult.Success -> {
                        it.copy(
                            error = null,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    fun onAction(action: Action) = when(action){
        is Action.ChangeDropDownState -> changeDropDownState(action.isExpanded, action.dropDownShown)
        is Action.ShowDialog -> showDialog(action.dialog)
        is Action.ChangeFromTime -> changeTripFromTime(action.hour, action.minute)
        is Action.ChangeFromDate -> changeTripFromDate(action.year, action.month, action.day)
        is Action.ChangeSearch -> changeSearch(action.qString)
        is Action.SelectStop -> selectStop(action.stop)
        is Action.SelectRoute -> selectRoute(action.route)
        is Action.UnselectQueryable -> unselectQueryable()
        is Action.SelectTrip -> runBlocking(Dispatchers.IO) { selectTrip(action.trip) }
        is Action.UnselectTrip -> unselectTrip()
        is Action.Search -> runBlocking(Dispatchers.IO) { search() }
        is Action.GetRoute -> getRoute(action.routeId)
    }
    private fun changeDropDownState(isExpanded: Boolean, dropDownShown: DropDowns?) = _state.update {
        it.copy(
            dropDownExpanded = isExpanded,
            dropDownShown = dropDownShown ?: _state.value.dropDownShown
        )
    }
    private fun showDialog(dialog: Dialogs?) = _state.update {
        it.copy(shownDialog = dialog)
    }
    private fun changeSearch(value: String) = _state.update {
        if (value == ""){
            it.copy(
                queryables = listOf(),
                selectedQueryable = null,
                searchString = value,
                dropDownShown = DropDowns.QueryableSelection,
                dropDownExpanded = false
            )
        } else {
            it.copy(
                queryables = (queryableRepository.queryables as ApiResult.Success).data.filter { queryable ->
                    when(queryable){
                        is Queryable.Stop -> {
                            queryable.name.contains(value, ignoreCase = true)
                        }
                        is Queryable.Route -> {
                            queryable.shortName.contains(value, ignoreCase = true)
                        }
                    }
                },
                selectedQueryable = null,
                searchString = value,
                dropDownShown = DropDowns.QueryableSelection,
                dropDownExpanded = true
            )
        }
    }
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun selectStop(stop: Queryable.Stop) {
        if (queryableRepository.queryables is ApiResult.Success){
            val stopExists: Boolean = (queryableRepository.queryables as ApiResult.Success<List<Queryable>>).data.filter {
                it is Queryable.Stop
            }.find {
                (it as Queryable.Stop).id == stop.id
            } != null
            if (stopExists) {
                _state.update {
                    it.copy(
                        selectedQueryable = stop,
                        searchString = stop.name,
                        dropDownExpanded = false,
                    )
                }
            } else {
                throw IllegalArgumentException("Selecting a stop not in the dataset is impossible")
            }
        } else {
            throw IllegalStateException("Can't select an item when the dataset has returned with an error")
        }
    }
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun selectRoute(route: Queryable.Route) {
        if (queryableRepository.queryables is ApiResult.Success){
            val routeExists: Boolean = (queryableRepository.queryables as ApiResult.Success<List<Queryable>>).data.filter {
                it is Queryable.Route
            }.find {
                (it as Queryable.Route).id == route.id
            } != null
            if (routeExists) {
                _state.update {
                    it.copy(
                        selectedQueryable = route,
                        searchString = route.shortName,
                        dropDownExpanded = false
                    )
                }
            } else {
                throw IllegalArgumentException("Selecting a route not in the dataset is impossible")
            }
        } else {
            throw IllegalStateException("Can't select an item when the dataset has returned with an error")
        }
    }
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private suspend fun selectTrip(trip: Trip) {
        if (queryableRepository.queryables is ApiResult.Success){
            val tripExists: Boolean = (tripsRepository.trips as ApiResult.Success).data.find {
                it.id == trip.id
            } != null
            if (tripExists) {
                _state.update {
                    it.copy(
                        selectedTrip = trip,
                        dropDownExpanded = false,
                        isLoading = true
                    )
                }

                tripsRepository.getShapes(trip = trip)
                tripsRepository.getStops(trip = trip)

                _state.update {
                    it.copy(isLoading = false)
                }

            } else {
                throw IllegalArgumentException("Selecting a queryable not in the dataset is impossible")
            }
        } else {
            throw IllegalStateException("Can't select an item when the dataset has returned with an error")
        }
    }
    private fun unselectQueryable() = _state.update {
        it.copy(
            selectedQueryable = null,
            searchString = ""
        )
    }
    private fun unselectTrip() = _state.update {
        it.copy(
            selectedTrip = null,
            dropDownShown = DropDowns.TripSelection,
            dropDownExpanded = true
        )
    }
    private fun changeTripFromDate(year: Int, month: Int, day: Int) = _state.update {
        it.copy(
            tripTimeConstraint = LocalDateTime.of(
                year, month,day,
                _state.value.tripTimeConstraint.hour,
                _state.value.tripTimeConstraint.minute
            ),
            shownDialog = null
        )
    }
    private fun changeTripFromTime(hour: Int, minute: Int) = _state.update {
        it.copy(
            tripTimeConstraint = LocalDateTime.of(
                _state.value.tripTimeConstraint.year,
                _state.value.tripTimeConstraint.month,
                _state.value.tripTimeConstraint.dayOfMonth,
                hour, minute
            ),
            shownDialog = null
        )
    }
    @Throws(IllegalStateException::class)
    private suspend fun search() = when(_state.value.selectedQueryable) {
        null -> throw IllegalStateException("Cannot search if no queryable is selected")
        else -> {
            _state.update {
                it.copy(
                    isLoading = true,
                    dropDownExpanded = true,
                    dropDownShown = DropDowns.TripSelection,
                )
            }

            tripsRepository.getTrips(
                dateTime = _state.value.tripTimeConstraint,
                selected = _state.value.selectedQueryable!!
            )

            _state.update {
                it.copy(
                    trips = (tripsRepository.trips as ApiResult.Success).data,
                    isLoading = false
                )
            }
        }
    }
    @Throws(NoSuchElementException::class)
    private fun getRoute(routeId: String): Queryable.Route{
        (queryableRepository.queryables as ApiResult.Success).data.forEach { queryable ->
            if (queryable is Queryable.Route && queryable.id == routeId) {
                return queryable
            }
        }
        throw NoSuchElementException("Item not found")
    }
    private fun toggleLoading() = _state.update {
        it.copy(isLoading = !it.isLoading)
    }
}