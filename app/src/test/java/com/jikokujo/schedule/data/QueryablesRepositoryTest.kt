package com.jikokujo.schedule.data

import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.repository.QueryableRepository

class QueryablesRepositoryTestImpl(queryablesIn: List<Queryable>): QueryableRepository {
    override var queryables: ApiResult<List<Queryable>> = ApiResult.Success(queryablesIn)

    override suspend fun getQueryables() {}
}