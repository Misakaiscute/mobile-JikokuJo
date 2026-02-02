package com.jikokujo.schedule.data.repository

import android.util.Log
import com.jikokujo.schedule.data.remote.QueryablesApi
import com.jikokujo.schedule.data.repository.QueryablesRepository
import com.jikokujo.core.data.ApiResult
import com.jikokujo.schedule.data.model.Queryable

class QueryablesRepositoryImpl(
    private val api: QueryablesApi
): QueryablesRepository {
    override lateinit var queryables: ApiResult<List<Queryable>>

    override suspend fun getQueryables() {
        val response = try {
             api.getQueryables()
        } catch (e: Exception) {
            this.queryables = ApiResult.Error("Something went wrong.")
            Log.e("EXCEPTION", e.message.toString())
            e.printStackTrace()
            return
        }
        val res: MutableList<Queryable> = mutableListOf()
        res.addAll(response.data?.routes as List<Queryable>)
        res.addAll(response.data.stops as List<Queryable>)
        this.queryables = ApiResult.Success(res)
    }
}