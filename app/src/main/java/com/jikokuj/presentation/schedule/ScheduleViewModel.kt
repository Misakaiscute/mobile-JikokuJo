package com.jikokuj.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikokuj.data.remote.Api
import com.jikokuj.data.remote.ApiResult
import com.jikokuj.domain.model.Queryable
import com.jikokuj.domain.repository.QueryableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleState(
    val queryables: List<Queryable> = listOf(),
    val selectedQueryable: Queryable? = null,
    val searchString: String = "",
    val isLoading: Boolean = false,
)
fun ScheduleState.queryableIsSelected(): Boolean = selectedQueryable != null

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
        viewModelScope.launch {
            val req = launch(Dispatchers.IO){ repository.getQueryables() }
            req.join()
            when(repository.queryables){
                is ApiResult.Success<List<Queryable>> -> {
                    _state.update {
                        it.copy(queryables = (repository.queryables as ApiResult.Success<List<Queryable>>).data)
                    }
                    toggleLoading()
                }
                is ApiResult.Error -> toggleLoading()
                else -> toggleLoading()
            }
        }
    }

    fun onAction(action: Action) = when(action){
        is Action.ChangeSearch -> changeSearch(action.qString)
        is Action.SelectQueryable -> selectQueryable(action.queryable)
        is Action.Search -> viewModelScope.launch { search() }
    }

    private fun changeSearch(value: String) = _state.update {
        it.copy(searchString = value)
    }
    private fun selectQueryable(queryable: Queryable) = _state.update {
        it.copy()
    }
    private suspend fun search(): Unit{

    }
    private fun toggleLoading() = _state.update {
        it.copy(isLoading = !it.isLoading)
    }
}