package com.jikokujo.schedule.data.repository

import android.util.Log
import com.jikokujo.schedule.data.remote.Api
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import okio.IOException
import retrofit2.HttpException

class QueryablesRepositoryImpl(private val api: Api): QueryableRepository {
    override lateinit var queryables: ApiResult<List<Queryable>>
    override var routesForStop: ApiResult<List<Queryable.Route>>? = null

    override suspend fun getQueryables() {
        val response = try {
             api.getQueryables()
        } catch (e: IOException) {
            this.queryables = ApiResult.Error("Something went wrong")
            Log.e("IO_EXCEPTION", e.message.toString())
            e.printStackTrace()
            return
        } catch (e: HttpException) {
            this.queryables = ApiResult.Error("Something went wrong")
            Log.e("HTTP_EXCEPTION", e.message.toString())
            e.printStackTrace()
            return
        }
        val res: MutableList<Queryable> = mutableListOf()
        res.addAll(response.data?.routes as List<Queryable>)
        res.addAll(response.data.stops as List<Queryable>)
        this.queryables = ApiResult.Success(res)
    }

    override suspend fun getRoutesForStop(stopId: String) {
        val response = try{
            api.getRoutesFromStop(stopId)
        } catch (e: IOException) {
            this.queryables = ApiResult.Error("Something went wrong")
            Log.e("IO_EXCEPTION", e.message.toString())
            e.printStackTrace()
            return
        } catch (e: HttpException) {
            this.queryables = ApiResult.Error("Something went wrong")
            Log.e("HTTP_EXCEPTION", e.message.toString())
            e.printStackTrace()
            return
        }
        this.routesForStop = ApiResult.Success(response.data!!.routes)
    }
}
class QueryablesRepositoryTestImpl: QueryableRepository {
    override lateinit var queryables: ApiResult<List<Queryable>>
    override var routesForStop: ApiResult<List<Queryable.Route>>? = null

    override suspend fun getQueryables() {
        queryables = ApiResult.Success(listOf(
            Queryable.Route(id = "001", name = "4-6-os villamos", type = 2, color = "000000"),
            Queryable.Route(id = "002", name = "M3-mas metró", type = 3, color = "8f0437"),
            Queryable.Route(id = "003", name = "73-mas trolibusz", type = 4, color = "0e349c"),
            Queryable.Route(id = "004", name = "119E busz", type = 1, color = "0b6324"),
            Queryable.Stop(id = "891", name = "Nyugati pályaudvar"),
            Queryable.Stop(id = "703", name = "Kálvin tér"),
            Queryable.Stop(id = "055M", name = "Blaha Lujza tér"),
        ))
    }

    override suspend fun getRoutesForStop(stopId: String) {
        TODO("Not yet implemented")
    }
}