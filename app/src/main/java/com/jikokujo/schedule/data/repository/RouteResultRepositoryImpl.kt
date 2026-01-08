package com.jikokujo.schedule.data.repository

import android.util.Log
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.remote.Api
import com.jikokujo.schedule.data.remote.ApiResult
import okio.IOException
import retrofit2.HttpException
import java.time.LocalDateTime
import kotlin.collections.mutableListOf

class RouteResultRepositoryImpl(private val api: Api): RouteResultRepository {
    override lateinit var possibleShapes: ApiResult<MutableMap<String, MutableList<Location.RoutePathPoint>>>
    override lateinit var trips: ApiResult<List<Trip>>

    override suspend fun getPossibleShapes(routeId: String) {
        val response = try {
            api.getPossibleShapesForRoute(routeId)
        } catch (e: IOException){
            this.possibleShapes = ApiResult.Error("Something went wrong")
            Log.e("IO_EXCEPTION", e.toString())
            e.printStackTrace()
            return
        } catch (e: HttpException){
            this.possibleShapes = ApiResult.Error("Something went wrong")
            Log.e("HTTP_EXCEPTION", e.toString())
            e.printStackTrace()
            return
        }
        this.possibleShapes = ApiResult.Success(mutableMapOf())
        response.data?.let {
            it.shapes.forEach { shape ->
                if (!(this.possibleShapes as ApiResult.Success<MutableMap<String, MutableList<Location.RoutePathPoint>>>).data.containsKey(shape.id)){
                    (this.possibleShapes as ApiResult.Success<MutableMap<String, MutableList<Location.RoutePathPoint>>>).data[shape.id] = mutableListOf(shape)
                } else {
                    (this.possibleShapes as ApiResult.Success<MutableMap<String, MutableList<Location.RoutePathPoint>>>).data[shape.id]!!.add(shape)
                }
            }
        }
    }
    override suspend fun getTrips(dateTime: LocalDateTime, routeId: String) {
        val response = try {
            api.getTrips(
                selectedRoute = routeId,
                year = dateTime.year,
                month = dateTime.monthValue,
                day = dateTime.dayOfMonth,
                hour = dateTime.hour,
                minute = dateTime.minute
            )
        } catch (e: IOException){
            this.possibleShapes = ApiResult.Error("Something went wrong")
            Log.e("IO_EXCEPTION", e.toString())
            e.printStackTrace()
            return
        } catch (e: HttpException){
            this.possibleShapes = ApiResult.Error("Something went wrong")
            Log.e("HTTP_EXCEPTION", e.toString())
            e.printStackTrace()
            return
        }
        this.trips = ApiResult.Success(response.data!!.trips)
    }
}