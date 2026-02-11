package com.jikokujo.schedule.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.remote.QueryablesApi
import com.jikokujo.core.data.ApiResult
import com.jikokujo.schedule.data.model.RoutePathPoint
import java.time.LocalDateTime
import kotlin.collections.mutableListOf

class TripsRepositoryImpl(private val api: QueryablesApi): TripsRepository {
    override var storedStops: MutableMap<String, ApiResult<List<StopWithLocationAndStopTime>>> = mutableMapOf()
    override var storedShapes: MutableMap<String, ApiResult<List<RoutePathPoint>>> = mutableMapOf()
    override lateinit var trips: ApiResult<List<Trip>>

    override suspend fun getShapes(trip: Trip) {
        if (!this.storedShapes.containsKey(trip.shapeId)){
            val response = try {
                api.getShapesForTrip(trip.id)
            } catch (e: Exception) {
                this.storedShapes[trip.shapeId] = ApiResult.Error("Something went wrong.")
                Log.e("EXCEPTION", e.toString())
                e.printStackTrace()
                return
            }
            response.data?.let {
                val shapes: MutableList<RoutePathPoint> = mutableListOf()
                for (i in 0..<it.shapes.count() / 2){
                    shapes.add(it.shapes[i])
                }
                this.storedShapes[trip.shapeId] = ApiResult.Success(shapes)
            }
        }
    }

    override suspend fun getStops(trip: Trip) {
        if (!this.storedStops.containsKey(trip.id)){
            val response = try {
                api.getStopsForTrip(trip.id)
            } catch (e: Exception){
                this.storedStops[trip.id] = ApiResult.Error("Something went wrong.")
                Log.e("EXCEPTION", e.toString())
                e.printStackTrace()
                return
            }
            response.data?.let {
                this.storedStops[trip.id] = ApiResult.Success(it.stops)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    override suspend fun getTrips(dateTime: LocalDateTime, selected: Queryable) {
        val response = try {
            when(selected){
                is Queryable.Stop -> {
                    api.getTripsFromStop(
                        stopId = selected.id,
                        year = dateTime.year.toString(),
                        month = String.format("%02d", dateTime.monthValue),
                        day = String.format("%02d", dateTime.dayOfMonth),
                        hour = String.format("%02d", dateTime.hour),
                        minute = String.format("%02d", dateTime.minute)
                    )
                }
                is Queryable.Route -> {
                    api.getTripsFromRoute(
                        routeId = selected.id,
                        year = dateTime.year.toString(),
                        month = String.format("%02d", dateTime.monthValue),
                        day = String.format("%02d", dateTime.dayOfMonth),
                        hour = String.format("%02d", dateTime.hour),
                        minute = String.format("%02d", dateTime.minute)
                    )
                }
            }
        } catch (e: Exception){
            this.trips = ApiResult.Error("Something went wrong.")
            Log.e("IO_EXCEPTION", e.toString())
            e.printStackTrace()
            return
        }
        this.trips = ApiResult.Success(response.data!!.trips)
    }
}