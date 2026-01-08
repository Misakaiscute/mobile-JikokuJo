package com.jikokujo.schedule.data.repository

import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.remote.ApiResult
import java.time.LocalDateTime

interface RouteResultRepository {
    var possibleShapes: ApiResult<MutableMap<String, MutableList<Location.RoutePathPoint>>>
    var trips: ApiResult<List<Trip>>
    suspend fun getPossibleShapes(routeId: String): Unit
    suspend fun getTrips(dateTime: LocalDateTime, routeId: String): Unit
}