package com.jikokujo.schedule.presentation

import com.jikokujo.schedule.data.MockTripsRepositoryImpl
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import com.jikokujo.schedule.presentation.map.TripAction
import com.jikokujo.schedule.presentation.map.TripInfoViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class TripInfoViewModelTest {
    private lateinit var viewModel: TripInfoViewModel
    @Before
    fun setUp() {
        val tripsRepository = MockTripsRepositoryImpl()
        this.viewModel = TripInfoViewModel(tripsRepository)

        runBlocking {
            tripsRepository.getTrips(
                dateTime = LocalDateTime.now(),
                selected = Queryable.Route(
                    id = "ROUTE_ID_1",
                    shortName = "1",
                    color = "000000",
                    type = 1
                )
            )
        }
    }
    @Test
    fun `fetching trip data on EXISTING trip selection`(){
        val route = Queryable.Route(
            id = "ROUTE",
            shortName = "SOMETHING",
            color = "ffffff",
            type = 1
        )
        val trip = Trip(
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
        runBlocking {
            viewModel.onAction(TripAction.SelectTrip(
                trip = trip,
                routeAssociated = route,
                selectedThrough = Queryable.Stop(
                    ids = listOf("1111"),
                    name = "Valami"
                )
            ))
        }
        assertTrue(
            "Stops must be set after selecting a trip",
            viewModel.state.value.stops.count() > 1
        )
        assertTrue(
            "PathPoints must be set after selecting a trip",
            viewModel.state.value.pathPoints.count() > 1
        )
        assertNull(
            "Error must be null after successful selection",
            viewModel.state.value.error
        )
        assertTrue(
            "Route associated must be to a Route after successful selection",
            viewModel.state.value.routeAssociated.hashCode() == route.hashCode()
        )
    }
    @Test
    fun `fetching trip data on NON-EXISTENT trip selection`(){
        val route = Queryable.Route(
            id = "ROUTE",
            shortName = "SOMETHING",
            color = "ffffff",
            type = 1
        )
        val trip = Trip(
            id = "NONEXISTENT_ID",
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

        val exception = assertThrows(IllegalArgumentException::class.java){
            runBlocking {
                viewModel.onAction(TripAction.SelectTrip(
                    trip = trip,
                    routeAssociated = route,
                    selectedThrough = Queryable.Stop(
                        ids = listOf("1111"),
                        name = "Valami"
                    )
                ))
            }
        }
        assertTrue(
            "IllegalArgumentException must be thrown",
            exception is IllegalArgumentException
        )
    }
    @Test
    fun `deselect trip after selection`(){
        val route = Queryable.Route(
            id = "ROUTE",
            shortName = "SOMETHING",
            color = "ffffff",
            type = 1
        )
        val trip = Trip(
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
        runBlocking {
            viewModel.onAction(TripAction.SelectTrip(
                trip = trip,
                routeAssociated = route,
                selectedThrough = Queryable.Stop(
                    ids = listOf("1111"),
                    name = "Valami"
                )
            ))
            viewModel.onAction(TripAction.UnselectTrip)
        }

        assertNull(
            "No error must remain when no trip is selected",
            viewModel.state.value.error
        )
        assertTrue(
            "Stops must be empty, when no trip is selected",
            viewModel.state.value.stops.count() == 0
        )
        assertTrue(
            "PathPoints must be empty, when no trip is selected",
            viewModel.state.value.pathPoints.count() == 0
        )
        assertNull(
            "No route can be associated, when no trip is selected",
            viewModel.state.value.routeAssociated
        )
    }
}