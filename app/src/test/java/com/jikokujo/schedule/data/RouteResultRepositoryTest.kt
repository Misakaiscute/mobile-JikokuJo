package com.jikokujo.schedule.data

import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.repository.RouteResultRepository
import java.time.LocalDateTime

class RouteResultRepositoryTestImpl: RouteResultRepository {
    override lateinit var possibleShapes: ApiResult<MutableMap<String, MutableList<Location.RoutePathPoint>>>
    override lateinit var trips: ApiResult<List<Trip>>

    override suspend fun getPossibleShapes(routeId: String) {}

    override suspend fun getTrips(dateTime: LocalDateTime, routeId: String) {
        this.trips = ApiResult.Success(listOf(
            Trip(
                id = "#01",
                shortName = "SHORTNAME",
                headsign = "HEADSIGN",
                shape = "SHAPE",
                stops = listOf(),
                wheelchairAccessible = 1,
                bikesAllowed = 1
            )
        ))
    }
}