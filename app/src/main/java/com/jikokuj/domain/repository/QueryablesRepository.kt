package com.jikokuj.domain.repository

import com.jikokuj.data.remote.ApiResult
import com.jikokuj.domain.model.Queryable
import com.jikokuj.domain.model.RouteDetailed

interface QueryableRepository {
    var queryables: ApiResult<List<Queryable>>?
    var searchResult: ApiResult<List<RouteDetailed>>?
    suspend fun getQueryables(): Unit
    suspend fun getRouteDetails(selected: Queryable): Unit
}