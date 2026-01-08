package com.jikokujo.schedule.presentation.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.repository.QueryableRepository
import com.jikokujo.schedule.data.repository.RouteResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


enum class Dialogs{
    DatePicker, TimePicker
}

data class ScheduleSearchState(
    val shownDialog: Dialogs? = null,
    val queryables: List<Queryable> = listOf(),
    val filteredQueryables: List<Queryable> = listOf(),
    val filteredQueryablesDropdownExpanded: Boolean = false,
    val tripsFrom: LocalDateTime = LocalDateTime.now(),
    val selectedQueryable: Queryable? = null,
    val searchString: String = "",
    val isLoading: Boolean = false,
)
sealed interface Action{
    data class ChangeDropdownState(val isExpanded: Boolean): Action
    data class ShowDialog(val dialog: Dialogs?): Action
    data class ChangeFromDate(val year: Int, val month: Int, val day: Int): Action
    data class ChangeFromTime(val hour: Int, val minute: Int): Action
    data class ChangeSearch(val qString: String): Action
    data class SelectQueryable(val queryable: Queryable): Action
    data object Search: Action
    data object UnselectQueryable: Action
}
@HiltViewModel
class ScheduleViewModel @Inject constructor(
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
        is Action.ChangeDropdownState -> changeDropDownState(action.isExpanded)
        is Action.ShowDialog -> showDialog(action.dialog)
        is Action.ChangeFromTime -> changeTripFromTime(action.hour, action.minute)
        is Action.ChangeFromDate -> changeTripFromDate(action.year, action.month, action.day)
        is Action.ChangeSearch -> changeSearch(action.qString)
        is Action.SelectQueryable -> selectQueryable(action.queryable)
        is Action.UnselectQueryable -> selectQueryable(null)
        is Action.Search -> viewModelScope.launch(Dispatchers.IO) { search() }
    }
    private fun changeDropDownState(isExpanded: Boolean) = _state.update {
        it.copy(filteredQueryablesDropdownExpanded = isExpanded)
    }
    private fun showDialog(dialog: Dialogs?) = _state.update {
        it.copy(shownDialog = dialog)
    }
    private fun changeSearch(value: String) = _state.update {
        if (value == ""){
            it.copy(
                searchString = value,
                filteredQueryables = listOf(),
                selectedQueryable = null
            )
        } else {
            it.copy(
                searchString = value,
                filteredQueryables = _state.value.queryables.filter { queryable ->
                    when(queryable){
                        is Queryable.Stop -> {
                            queryable.name.contains(value, ignoreCase = true)
                        }
                        is Queryable.Route -> {
                            queryable.name.contains(value, ignoreCase = true)
                        }
                    }
                },
                selectedQueryable = null
            )
        }
    }
    private fun selectQueryable(queryable: Queryable?) = _state.update {
        it.copy(
            selectedQueryable = queryable,
            searchString = when(queryable){
                is Queryable.Stop -> queryable.name
                is Queryable.Route -> queryable.name
                else -> ""
            }
        )
    }
    private fun changeTripFromDate(year: Int, month: Int, day: Int) = _state.update {
        it.copy(
            tripsFrom = LocalDateTime.of(
                year, month,day,
                _state.value.tripsFrom.hour,
                _state.value.tripsFrom.minute
            ),
            shownDialog = null
        )
    }
    private fun changeTripFromTime(hour: Int, minute: Int) = _state.update {
        it.copy(
            tripsFrom = LocalDateTime.of(
                _state.value.tripsFrom.year,
                _state.value.tripsFrom.month,
                _state.value.tripsFrom.dayOfMonth,
                hour, minute
            ),
            shownDialog = null
        )
    }
    private suspend fun search() {

    }
    private fun toggleLoading() = _state.update {
        it.copy(isLoading = !it.isLoading)
    }
}