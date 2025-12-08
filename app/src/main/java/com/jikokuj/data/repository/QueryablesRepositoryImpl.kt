package com.jikokuj.data.repository

import com.jikokuj.data.remote.Api
import com.jikokuj.data.remote.ApiResult
import com.jikokuj.domain.model.Queryable
import com.jikokuj.domain.model.RouteDetailed
import com.jikokuj.domain.repository.QueryableRepository
import okio.IOException
import retrofit2.Call
import retrofit2.HttpException

class QueryablesRepositoryImpl(private val api: Api): QueryableRepository {
    override var queryables: ApiResult<List<Queryable>>? = null
    override var searchResult: ApiResult<List<RouteDetailed>>? = null

    override suspend fun getQueryables() {
        val response = try {
             api.getQueryables().execute()
        } catch (e: IOException) {
            this.queryables = ApiResult.Error("Something went wrong")
            return
        } catch (e: HttpException) {
            this.queryables = ApiResult.Error("Something went wrong")
            return
        }
        if(response.isSuccessful && response.body() != null){
            this.queryables = ApiResult.Success(response.body()!!)
        } else {
            this.queryables = ApiResult.Error("Something went wrong")
        }
    }

    override suspend fun getRouteDetails(selected: Queryable) {
        val apiEndpoint: Call<List<RouteDetailed>> = when(selected) {
            is Queryable.Route -> api.getRouteDetailsFromRoute(selected.id)
            is Queryable.Stop -> api.getRouteDetailsFromStop(selected.id)
        }
        val response = try {
            apiEndpoint.execute()
        } catch (e: IOException) {
            this.queryables = ApiResult.Error("Something went wrong")
            return
        } catch (e: HttpException) {
            this.queryables = ApiResult.Error("Something went wrong")
            return
        }
        if(response.isSuccessful && response.body() != null){
            this.searchResult = ApiResult.Success(response.body()!!)
        } else {
            this.queryables = ApiResult.Error("Something went wrong")
        }
    }
}