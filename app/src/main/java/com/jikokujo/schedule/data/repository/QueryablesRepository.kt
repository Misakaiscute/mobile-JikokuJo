package com.jikokujo.schedule.data.repository

import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable

interface QueryableRepository {
    var queryables: ApiResult<List<Queryable>>
    var routesForStop: ApiResult<List<Queryable.Route>>?
    suspend fun getQueryables(): Unit
    suspend fun getRoutesForStop(stopId: String): Unit
}