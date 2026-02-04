package com.jikokujo.schedule.data

import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.core.data.ApiResult
import com.jikokujo.schedule.data.repository.TripsRepository
import java.time.LocalDateTime

class MockTripsRepositoryImpl: TripsRepository {
    override var storedStops: MutableMap<String, ApiResult<List<StopWithLocationAndStopTime>>> = mutableMapOf()
    override var storedShapes: MutableMap<String, ApiResult<List<Location.RoutePathPoint>>> = mutableMapOf()
    override lateinit var trips: ApiResult<List<Trip>>

    override suspend fun getShapes(trip: Trip) {
        this.storedShapes.clear()
        this.storedShapes[trip.shapeId] = ApiResult.Success(
            listOf(
                Location.RoutePathPoint("asd0", 11.0, 11.0),
                Location.RoutePathPoint("asd1", 11.1, 11.1),
                Location.RoutePathPoint("asd2", 11.2, 11.2),
                Location.RoutePathPoint("asd3", 11.3, 11.3),
                Location.RoutePathPoint("asd4", 11.4, 11.4),
                Location.RoutePathPoint("asd5", 11.5, 11.5),
                Location.RoutePathPoint("asd6", 11.6, 11.6),
                Location.RoutePathPoint("asd7", 11.7, 11.7),
                Location.RoutePathPoint("asd8", 11.8, 11.8),
                Location.RoutePathPoint("asd9", 11.9, 11.9),
            )
        )
    }

    override suspend fun getStops(trip: Trip) {
        this.storedStops.clear()
        this.storedStops[trip.id] = ApiResult.Success(
            listOf(
                StopWithLocationAndStopTime(
                    id = "STOP_1",
                    name = "STOP NUMBER 1",
                    location = Location.Stop(11.0, 11.0),
                    arrivalTime = 710,
                    order = 1
                ),
                StopWithLocationAndStopTime(
                    id = "STOP_2",
                    name = "STOP NUMBER 2",
                    location = Location.Stop(12.0, 12.0),
                    arrivalTime = 720,
                    order = 2
                ),
                StopWithLocationAndStopTime(
                    id = "STOP_3",
                    name = "STOP NUMBER 3",
                    location = Location.Stop(13.0, 13.0),
                    arrivalTime = 730,
                    order = 3,
                ),
                StopWithLocationAndStopTime(
                    id = "STOP_4",
                    name = "STOP NUMBER 4",
                    location = Location.Stop(14.0, 14.0),
                    arrivalTime = 740,
                    order = 3
                ),
            )
        )
    }

    override suspend fun getTrips(dateTime: LocalDateTime, selected: Queryable) {
        this.trips = ApiResult.Success(listOf(
            Trip(
                id = "TRIP_ID_1",
                headSign = "TRIP HEADSIGN",
                routeId = "ROUTE_ID_1",
                shapeId = "SHAPE_ID_1",
                stops = listOf(
                    StopWithLocationAndStopTime(
                        id = "STOP_1",
                        name = "STOP NUMBER 1",
                        location = Location.Stop(11.0, 11.0),
                        arrivalTime = 710,
                        order = 1
                    ),
                    StopWithLocationAndStopTime(
                        id = "STOP_4",
                        name = "STOP NUMBER 4",
                        location = Location.Stop(14.0, 14.0),
                        arrivalTime = 740,
                        order = 4
                    ),
                ),
                wheelchairAccessible = 1,
                bikesAllowed = 1,
                directionId = 1
            )
        ))
    }
}