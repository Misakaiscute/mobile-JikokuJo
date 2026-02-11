package com.jikokujo.schedule.data.repository

import com.jikokujo.core.data.ApiResult
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.RoutePathPoint
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import java.time.LocalDateTime

interface TripsRepository {
    var storedStops: MutableMap<String, ApiResult<List<StopWithLocationAndStopTime>>>
    var storedShapes: MutableMap<String, ApiResult<List<RoutePathPoint>>>
    var trips: ApiResult<List<Trip>>
    suspend fun getShapes(trip: Trip)
    suspend fun getStops(trip: Trip)
    suspend fun getTrips(dateTime: LocalDateTime, selected: Queryable)
}