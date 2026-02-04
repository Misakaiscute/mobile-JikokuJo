package com.jikokujo.schedule.data

import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.core.data.ApiResult
import com.jikokujo.schedule.data.repository.QueryablesRepository

class MockQueryablesRepositoryImpl(queryablesIn: List<Queryable>): QueryablesRepository {
    override var queryables: ApiResult<List<Queryable>> = ApiResult.Success(queryablesIn)

    override suspend fun getQueryables() {}
}