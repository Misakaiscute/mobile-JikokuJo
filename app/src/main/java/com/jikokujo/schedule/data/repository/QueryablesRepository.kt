package com.jikokujo.schedule.data.repository

import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable

interface QueryablesRepository {
    var queryables: ApiResult<List<Queryable>>

    suspend fun getQueryables()
}