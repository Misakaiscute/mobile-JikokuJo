package com.jikokujo.schedule.data.repository

import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable

interface QueryableRepository {
    var queryables: ApiResult<List<Queryable>>
    suspend fun getQueryables(): Unit
}