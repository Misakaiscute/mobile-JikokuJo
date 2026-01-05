package com.jikokujo.schedule.data.repository

import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.RouteDetailed

interface QueryableRepository {
    var queryables: ApiResult<List<Queryable>>
    var searchResult: ApiResult<List<RouteDetailed>>
    suspend fun getQueryables(): Unit
    suspend fun getRouteDetails(selected: Queryable): Unit
}