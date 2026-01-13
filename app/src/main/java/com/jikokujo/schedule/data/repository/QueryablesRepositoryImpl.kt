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