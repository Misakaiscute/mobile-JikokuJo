package com.jikokujo.schedule.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.repository.QueryableRepository
import com.jikokujo.schedule.data.repository.RouteResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

enum class Dialogs{
    DatePicker, TimePicker
}
enum class DropDowns{
    QueryableSelection, RouteSelection, TripSelection
}

data class ScheduleSearchState(
    val queryables: List<Queryable> = listOf(),
    val searchString: String = "",
    val selectedStop: Queryable.Stop? = null,
    val selectedRoute: Queryable.Route? = null,
    val tripTimeConstraint: LocalDateTime = LocalDateTime.now(),
    val trips: List<Trip> = listOf(),
    val selectedTrip: Trip? = null,
    val dropDownExpanded: Boolean = false,
    val dropDownShown: DropDowns? = null,
    val shownDialog: Dialogs? = null,
    val isLoading: Boolean = false,
)

sealed interface Action{
    data class ChangeDropDownState(val isExpanded: Boolean): Action
    data class ShowDialog(val dialog: Dialogs?): Action
    data class ChangeFromDate(val year: Int, val month: Int, val day: Int): Action
    data class ChangeFromTime(val hour: Int, val minute: Int): Action
    data class ChangeSearch(val qString: String): Action
    data class SelectRoute(val route: Queryable.Route): Action
    data class SelectStop(val stop: Queryable.Stop): Action
    data class SelectTrip(val trip: Trip): Action
    data object UnselectQueryable: Action
    data object Search: Action
}
@HiltViewModel
class ScheduleSearchViewModel @Inject constructor(
    private val queryableRepository: QueryableRepository,
    private val routeResultRepository: RouteResultRepository
): ViewModel() {
    private val _state = MutableStateFlow(ScheduleSearchState())
    val state = _state.asStateFlow()

    init {
        toggleLoading()
        viewModelScope.launch(Dispatchers.IO) {
            queryableRepository.getQueryables()
            when(queryableRepository.queryables){
                is ApiResult.Success<List<Queryable>> -> {
                    _state.update {
                        it.copy(queryables = (queryableRepository.queryables as ApiResult.Success<List<Queryable>>).data)
                    }
                    toggleLoading()
                }
                is ApiResult.Error -> toggleLoading()
            }
        }
    }
    fun onAction(action: Action) = when(action){
        is Action.ChangeDropDownState -> changeDropDownState(action.isExpanded)
        is Action.ShowDialog -> showDialog(action.dialog)
        is Action.ChangeFromTime -> changeTripFromTime(action.hour, action.minute)
        is Action.ChangeFromDate -> changeTripFromDate(action.year, action.month, action.day)
        is Action.ChangeSearch -> changeSearch(action.qString)
        is Action.SelectStop -> selectStop(action.stop)
        is Action.SelectRoute -> selectRoute(action.route)
        is Action.SelectTrip -> selectTrip(action.trip)
        is Action.UnselectQueryable -> unselectQueryable()
        is Action.Search -> search()
    }
    private fun changeDropDownState(isExpanded: Boolean) = _state.update {
        it.copy(dropDownExpanded = isExpanded)
    }
    private fun showDialog(dialog: Dialogs?) = _state.update {
        it.copy(shownDialog = dialog)
    }
    private fun changeSearch(value: String) = _state.update {
        if (value == ""){
            it.copy(
                queryables = listOf(),
                selectedStop = null,
                selectedRoute = null,
                searchString = value,
                dropDownShown = null,
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
                            queryable.name.contains(value, ignoreCase = true)
                        }
                    }
                },
                selectedStop = null,
                selectedRoute = null,
                searchString = value,
                dropDownShown = DropDowns.QueryableSelection,
                dropDownExpanded = true
            )
        }
    }
    private fun selectStop(stop: Queryable.Stop) = _state.update {
        it.copy(
            selectedStop = stop,
            searchString = stop.name
        )
    }
    private fun selectRoute(route: Queryable.Route) = _state.update {
        it.copy(
            selectedRoute = route,
            searchString = route.name
        )
    }
    private fun unselectQueryable() = _state.update {
        it.copy(
            selectedStop = null,
            selectedRoute = null,
            searchString = ""
        )
    }
    private fun selectTrip(trip: Trip) = _state.update {
        it.copy(selectedTrip = trip)
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
    private fun search() {
        if (_state.value.selectedRoute != null){
            _state.update {
                it.copy(
                    dropDownExpanded = true,
                    dropDownShown = DropDowns.TripSelection,
                    isLoading = true
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                async { routeResultRepository.getTrips(
                    dateTime = _state.value.tripTimeConstraint,
                    routeId = _state.value.selectedRoute!!.id
                )}
                async { routeResultRepository.getPossibleShapes(
                    routeId = _state.value.selectedRoute!!.id
                )}
            }
            _state.update {
                it.copy(
                    trips = (routeResultRepository.trips as ApiResult.Success).data,
                    isLoading = false
                )
            }
        } else if (_state.value.selectedStop != null) {
            _state.update {
                it.copy(
                    isLoading = true,
                    dropDownExpanded = true,
                    dropDownShown = DropDowns.RouteSelection
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                queryableRepository.getRoutesForStop(_state.value.selectedStop!!.id)
            }
            _state.update {
                it.copy(
                    queryables = (queryableRepository.routesForStop as ApiResult.Success).data,
                    isLoading = false
                )
            }
        } else {
            throw IllegalStateException("Cannot search if selectedStop and selectedRoute are both null")
        }
    }
    private fun toggleLoading() = _state.update {
        it.copy(isLoading = !it.isLoading)
    }
}