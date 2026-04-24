package com.jikokujo.schedule.data

import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.model.RoutePathPoint
import com.jikokujo.schedule.data.repository.TripsRepository
import java.time.LocalDateTime

class MockTripsRepository : TripsRepository {
    var getStops: List<StopWithLocationAndStopTime> = emptyList()
    var getShapes: List<RoutePathPoint> = emptyList()
    var getTrips: ApiResult<List<Trip>> = ApiResult.Success(listOf())
    override var trips: ApiResult<List<Trip>> = ApiResult.Success(emptyList())

    override suspend fun getShapes(trip: Trip) = getShapes
    override suspend fun getStops(trip: Trip) = getStops
    override suspend fun getTrips(dateTime: LocalDateTime, selected: Queryable) {
        this.trips = getTrips
    }
}