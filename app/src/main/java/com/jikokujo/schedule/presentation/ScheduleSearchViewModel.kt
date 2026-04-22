package com.jikokujo.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.repository.QueryablesRepository
import com.jikokujo.schedule.data.repository.TripsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

sealed interface ScheduleAction{
    data class ChangeDropDownState(val isExpanded: Boolean, val dropDownShown: DropDowns? = null): ScheduleAction
    data class ShowDialog(val dialog: Dialogs?): ScheduleAction
    data class ChangeFromDate(val year: Int, val month: Int, val day: Int): ScheduleAction
    data class ChangeFromTime(val hour: Int, val minute: Int): ScheduleAction
    data class ChangeSearch(val string: String): ScheduleAction
    data class SelectRoute(val route: Queryable.Route): ScheduleAction
    data class SelectStop(val stop: Queryable.Stop): ScheduleAction
    data class SelectTrip(val trip: Trip): ScheduleAction
    data object UnselectQueryable: ScheduleAction
    data object UnselectTrip: ScheduleAction
    data object Search: ScheduleAction
    data object RetryFetchInitialData: ScheduleAction
}
@HiltViewModel
class ScheduleSearchViewModel @Inject constructor(
    private val queryableRepository: QueryablesRepository,
    private val tripsRepository: TripsRepository
): ViewModel() {
    private var _queryableFilteringJob: Job? = null

    private val _state = MutableStateFlow(ScheduleSearchState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchQueryables()
        }
    }

    suspend fun onAction(action: ScheduleAction) = when(action){
        is ScheduleAction.ChangeDropDownState -> changeDropDownState(action.isExpanded, action.dropDownShown)
        is ScheduleAction.ShowDialog -> showDialog(action.dialog)
        is ScheduleAction.ChangeFromTime -> changeTripFromTime(action.hour, action.minute)
        is ScheduleAction.ChangeFromDate -> changeTripFromDate(action.year, action.month, action.day)
        is ScheduleAction.ChangeSearch -> withContext(Dispatchers.Default) { changeSearchString(action.string) }
        is ScheduleAction.SelectStop -> selectStop(action.stop)
        is ScheduleAction.SelectRoute -> selectRoute(action.route)
        is ScheduleAction.UnselectQueryable -> unselectQueryable()
        is ScheduleAction.SelectTrip -> withContext(Dispatchers.IO) { selectTrip(action.trip) }
        is ScheduleAction.UnselectTrip -> unselectTrip()
        is ScheduleAction.Search -> withContext(Dispatchers.IO) { search() }
        is ScheduleAction.RetryFetchInitialData -> withContext(Dispatchers.IO) { fetchQueryables() }
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
    private fun changeSearchString(toValue: String){
        _queryableFilteringJob?.cancel()
        _state.update {
            it.copy(searchString = toValue)
        }
        _queryableFilteringJob = viewModelScope.launch {
            delay(500)
            filterQueryables(toValue)
        }
    }
    private fun filterQueryables(currentSearchString: String) = _state.update {
        if (currentSearchString.isBlank()){
            it.copy(
                queryables = listOf(),
                selectedQueryable = null,
                dropDownShown = DropDowns.QueryableSelection,
                dropDownExpanded = false
            )
        } else {
            val filteredItems = (queryableRepository.queryables as ApiResult.Success).data.filter { queryable ->
                when(queryable){
                    is Queryable.Stop -> queryable.name.contains(currentSearchString, ignoreCase = true)
                    is Queryable.Route -> queryable.shortName.contains(currentSearchString, ignoreCase = true)
                }
            }.take(100)
            it.copy(
                queryables = filteredItems,
                selectedQueryable = null,
                dropDownShown = DropDowns.QueryableSelection,
                dropDownExpanded = !filteredItems.isEmpty()
            )
        }
    }
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    private fun selectStop(stop: Queryable.Stop) {
        if (queryableRepository.queryables is ApiResult.Success){
            val stopExists: Boolean = (queryableRepository.queryables as ApiResult.Success<List<Queryable>>).data
                .filterIsInstance<Queryable.Stop>()
                .find {
                    it.name == stop.name
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
        if (queryableRepository.queryables is ApiResult.Success<*>){
            val routeExists: Boolean = (queryableRepository.queryables as ApiResult.Success<List<Queryable>>).data
                .filterIsInstance<Queryable.Route>()
                .find {
                    it.id == route.id
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
    private fun selectTrip(trip: Trip) {
        if (tripsRepository.trips is ApiResult.Success){
            val tripExists: Boolean = (tripsRepository.trips as ApiResult.Success).data.find {
                it.id == trip.id
            } != null
            if (tripExists) {
                _state.update {
                    it.copy(
                        selectedTrip = trip,
                        dropDownExpanded = false,
                    )
                }
            } else {
                throw IllegalArgumentException("Selecting a queryable not in the dataset is impossible")
            }
        } else {
            throw IllegalStateException("Can't select an item when the dataset has returned with an error")
        }
    }
    private fun unselectQueryable() = {
        _state.update {
            it.copy(
                selectedQueryable = null,
                searchString = ""
            )
        }
        changeSearchString("")
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
                    dropDownExpanded = false,
                    dropDownShown = DropDowns.TripSelection,
                    error = null,
                    isLoading = true,
                )
            }

            tripsRepository.getTrips(
                dateTime = _state.value.tripTimeConstraint,
                selected = _state.value.selectedQueryable!!
            )

            _state.update {
                when(tripsRepository.trips){
                    is ApiResult.Error -> {
                        it.copy(
                            trips = listOf(),
                            error = "${(tripsRepository.trips as ApiResult.Error).errorMsg} Retry?",
                            isLoading = false
                        )
                    }
                    is ApiResult.Success -> {
                        it.copy(
                            trips = (tripsRepository.trips as ApiResult.Success).data.sortedBy { trip ->
                                trip.stops[0].arrivalTime
                            },
                            dropDownExpanded = true,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    private suspend fun fetchQueryables() {
        _state.update {
            it.copy(
                error = null,
                isLoading = true
            )
        }
        queryableRepository.getQueryables()
        _state.update {
            when (queryableRepository.queryables) {
                is ApiResult.Error -> {
                    it.copy(
                        error = "${(queryableRepository.queryables as ApiResult.Error).errorMsg} Retry?",
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
    @Throws(NoSuchElementException::class)
    fun getRoute(routeId: String): Queryable.Route{
        (queryableRepository.queryables as ApiResult.Success).data.forEach { queryable ->
            if (queryable is Queryable.Route && queryable.id == routeId) {
                return queryable
            }
        }
        throw NoSuchElementException("Item not found")
    }
}