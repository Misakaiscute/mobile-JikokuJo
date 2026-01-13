package com.jikokujo.schedule.data

import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.repository.QueryableRepository

class QueryablesRepositoryTestImpl(
    queryablesIn: List<Queryable>,
    routesForStopIn: List<Queryable.Route>
): QueryableRepository {
    override var queryables: ApiResult<List<Queryable>> = ApiResult.Success(queryablesIn)
    override var routesForStop: ApiResult<List<Queryable.Route>>? = ApiResult.Success(routesForStopIn)

    override suspend fun getQueryables() {}

    override suspend fun getRoutesForStop(stopId: String) {}
}