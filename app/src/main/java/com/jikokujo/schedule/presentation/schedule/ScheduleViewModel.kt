package com.jikokujo.schedule.presentation.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.repository.QueryableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleState(
    val queryables: List<Queryable> = listOf(),
    val filteredQueryables: List<Queryable> = listOf(),
    val selectedQueryable: Queryable? = null,
    val searchString: String = "",
    val isLoading: Boolean = false,
)

sealed interface Action{
    data class ChangeSearch(val qString: String): Action
    data class SelectQueryable(val queryable: Queryable): Action
    data object Search: Action
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private var repository: QueryableRepository
): ViewModel() {
    private val _state = MutableStateFlow<ScheduleState>(ScheduleState())
    val state = _state.asStateFlow()

    init {
        toggleLoading()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getQueryables()
            Log.i("Queryables fetched: ", repository.queryables.toString())
            when(repository.queryables){
                is ApiResult.Success<List<Queryable>> -> {
                    _state.update {
                        it.copy(queryables = (repository.queryables as ApiResult.Success<List<Queryable>>).data)
                    }
                    toggleLoading()
                }
                is ApiResult.Error -> toggleLoading()
            }
        }
    }

    fun onAction(action: Action) = when(action){
        is Action.ChangeSearch -> changeSearch(action.qString)
        is Action.SelectQueryable -> selectQueryable(action.queryable)
        is Action.Search -> viewModelScope.launch(Dispatchers.IO) { search() }
    }

    private fun changeSearch(value: String) = _state.update {
        if (value == ""){
            it.copy(
                searchString = value,
                filteredQueryables = listOf()
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
                }
            )
        }
    }
    private fun selectQueryable(queryable: Queryable) = _state.update {
        it.copy(selectedQueryable = queryable)
    }
    private suspend fun search(): Unit{

    }
    private fun toggleLoading() = _state.update {
        it.copy(isLoading = !it.isLoading)
    }
}