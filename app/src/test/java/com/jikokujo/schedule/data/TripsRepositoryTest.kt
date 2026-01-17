package com.jikokujo.schedule.data

import androidx.compose.runtime.saveable.listSaver
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.data.remote.ApiResult
import com.jikokujo.schedule.data.repository.TripsRepository
import java.time.LocalDateTime

class TripsRepositoryTestImpl: TripsRepository {
    override var storedStops: MutableMap<String, ApiResult<List<StopWithLocationAndStopTime>>> = mutableMapOf()
    override var storedShapes: MutableMap<String, ApiResult<List<Location.RoutePathPoint>>> = mutableMapOf()
    override lateinit var trips: ApiResult<List<Trip>>

    override suspend fun getShapes(trip: Trip) {}

    override suspend fun getStops(trip: Trip) {}

    override suspend fun getTrips(dateTime: LocalDateTime, selected: Queryable) {
        this.trips = ApiResult.Success(listOf())
    }
}