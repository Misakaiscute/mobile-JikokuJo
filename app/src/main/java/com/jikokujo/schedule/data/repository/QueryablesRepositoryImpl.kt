package com.jikokujo.schedule.data.repository

import android.util.Log
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.remote.QueryablesApi

class QueryablesRepositoryImpl(
    private val api: QueryablesApi
): QueryablesRepository {
    private lateinit var queryables: ApiResult<List<Queryable>>

    override suspend fun getQueryables(): ApiResult<List<Queryable>> {
        if (!this::queryables.isInitialized || queryables is ApiResult.Error){
            val response = try {
                api.getQueryables()
            } catch (e: Exception) {
                this.queryables = ApiResult.Error("Valami hiba történt.")
                Log.e("EXCEPTION", e.message.toString())
                e.printStackTrace()
                return this.queryables
            }
            val res: MutableList<Queryable> = mutableListOf()
            res.addAll(response.data?.routes as List<Queryable>)
            res.addAll(response.data.stops as List<Queryable>)
            this.queryables = ApiResult.Success(res)

            return this.queryables
        }
        return this.queryables
    }
}