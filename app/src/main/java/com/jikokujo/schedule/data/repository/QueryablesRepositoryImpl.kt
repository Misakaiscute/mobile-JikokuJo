package com.jikokujo.schedule.data.repository

import android.net.http.QuicException
import android.util.Log
import com.jikokujo.schedule.data.remote.Api
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.RouteDetailed
import okio.IOException
import retrofit2.HttpException

class QueryablesRepositoryImpl(private val api: Api): QueryableRepository {
    override lateinit var queryables: ApiResult<List<Queryable>>
    override lateinit var searchResult: ApiResult<List<RouteDetailed>>

    override suspend fun getQueryables() {
        val response = try {
             api.getQueryables()
        } catch (e: IOException) {
            this.queryables = ApiResult.Error("Something went wrong: IO Exception: ${e.printStackTrace()}")
            return
        } catch (e: HttpException) {
            this.queryables = ApiResult.Error("Something went wrong: HttpException: ${e.printStackTrace()}")
            return
        }
        Log.d("ROUTES", response.data?.routes.toString())
        Log.d("STOPS", response.data?.stops.toString())
        val res: MutableList<Queryable> = mutableListOf()
        res.addAll(response.data?.routes as List<Queryable>)
        res.addAll(response.data.stops as List<Queryable>)
        this.queryables = ApiResult.Success(res)
    }

    override suspend fun getRouteDetails(selected: Queryable) {
        //TODO: IMPLEMENT
//        val apiEndpoint = when(selected) {
//            is Queryable.Route -> api.getRouteDetailsFromRoute(selected.id)
//            is Queryable.Stop -> api.getRouteDetailsFromStop(selected.id)
//        }
//        val response = try {
//            apiEndpoint.execute()
//        } catch (e: IOException) {
//            this.queryables = ApiResult.Error("Something went wrong")
//            return
//        } catch (e: HttpException) {
//            this.queryables = ApiResult.Error("Something went wrong")
//            return
//        }
//        if(response.isSuccessful && response.body() != null){
//            this.searchResult = ApiResult.Success(response.body()!!)
//        } else {
//            this.queryables = ApiResult.Error("Something went wrong")
//        }
    }
}