package com.jikokujo.schedule.data.repository

import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.remote.ApiResult
import java.time.LocalDateTime

interface TripsRepository {
    var storedStops: MutableMap<String, ApiResult<List<StopWithLocationAndStopTime>>>
    var storedShapes: MutableMap<String, ApiResult<List<Location.RoutePathPoint>>>
    var trips: ApiResult<List<Trip>>
    suspend fun getShapes(trip: Trip): Unit
    suspend fun getStops(trip: Trip): Unit
    suspend fun getTrips(dateTime: LocalDateTime, selected: Queryable): Unit
}