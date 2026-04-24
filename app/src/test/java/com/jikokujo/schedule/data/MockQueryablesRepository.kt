package com.jikokujo.schedule.data

import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.repository.QueryablesRepository

class MockQueryablesRepository(): QueryablesRepository {
    var queryablesRequestResult: ApiResult<List<Queryable>> = ApiResult.Success(listOf())
    override suspend fun getQueryables() = queryablesRequestResult
}