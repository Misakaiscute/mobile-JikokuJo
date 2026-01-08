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
class QueryablesRepositoryTestImpl: QueryableRepository {
    override lateinit var queryables: ApiResult<List<Queryable>>
    override lateinit var searchResult: ApiResult<List<RouteDetailed>>

    override suspend fun getQueryables() {
        queryables = ApiResult.Success(listOf(
            Queryable.Route(id = "001", name = "4-6-os villamos", type = 2),
            Queryable.Route(id = "002", name = "M3-mas metró", type = 3),
            Queryable.Route(id = "003", name = "73-mas trolibusz", type = 4),
            Queryable.Route(id = "004", name = "119E busz", type = 1),
            Queryable.Stop(idsAssociated = listOf("001", "002"), name = "Nyugati pályaudvar"),
            Queryable.Stop(idsAssociated = listOf("003", "004"), name = "Kálvin tér"),
            Queryable.Stop(idsAssociated = listOf("001", "002", "003", "004"), name = "Blaha Lujza tér"),
        ))
    }
    override suspend fun getRouteDetails(selected: Queryable) {
        return
    }
}