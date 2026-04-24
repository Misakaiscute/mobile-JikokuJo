package com.jikokujo.map.presentation

import com.jikokujo.core.data.MockUserRepository
import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.MockTripsRepository
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.RoutePathPoint
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class TripInfoViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var tripsRepository: MockTripsRepository
    private lateinit var userRepository: MockUserRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tripsRepository = MockTripsRepository()
        userRepository = MockUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    private fun route(id: String = "r1", shortName: String = "42") =
        Queryable.Route(id = id, shortName = shortName, type = 1, color = "FF0000")

    private fun stop(id: String = "s1", name: String = "Main St") =
        Queryable.Stop(ids = listOf(id), name = name)

    private fun trip(id: String = "t1") = Trip(
        id = id,
        headSign = "Test",
        routeId = "r1",
        shapeId = "shape1",
        stops = listOf(stopWithTime()),
        wheelchairAccessible = 0,
        bikesAllowed = 0,
        directionId = 0
    )

    private fun stopWithTime(arrivalTime: Int = 600) = StopWithLocationAndStopTime(
        id = "s1",
        name = "Test stop",
        location = Location.Stop(lat = 47.0, lon = 19.0),
        arrivalTime = arrivalTime,
        order = 1
    )

    private fun pathPoint() = RoutePathPoint(
        distanceTraveled = 2,
        location = Location.Auxiliary(lat = 47.0, lon = 19.0)
    )


    private fun TestScope.buildViewModel(): TripInfoViewModel {
        val vm = TripInfoViewModel(
            tripsRepository = tripsRepository,
            userRepository = userRepository,
            ioDispatcher = testDispatcher
        )
        testScheduler.advanceUntilIdle()
        return vm
    }


    @Test
    fun `init - favourites flow emission updates state`() = runTest {
        val vm = buildViewModel()
        val favourites = listOf(Favourite(route = route(), atMins = 480))

        userRepository.favourites.emit(favourites)
        testScheduler.advanceUntilIdle()

        assertEquals(favourites, vm.state.value.favourites)
    }

    @Test
    fun `init - favourites flow null emission is reflected in state`() = runTest {
        val vm = buildViewModel()
        userRepository.favourites.emit(listOf(Favourite(route = route(), atMins = 100)))
        testScheduler.advanceUntilIdle()

        userRepository.favourites.emit(null)
        testScheduler.advanceUntilIdle()

        assertNull(vm.state.value.favourites)
    }

    // -------------------------------------------------------------------------
    // ShowTripInfo / HideTripInfo
    // -------------------------------------------------------------------------

    @Test
    fun `ShowTripInfo - sets tripInfoShown to true`() = runTest {
        val vm = buildViewModel()

        vm.onAction(TripAction.ShowTripInfo)

        assertTrue(vm.state.value.tripInfoShown)
    }

    @Test
    fun `HideTripInfo - sets tripInfoShown to false`() = runTest {
        val vm = buildViewModel()
        vm.onAction(TripAction.ShowTripInfo)

        vm.onAction(TripAction.HideTripInfo)

        assertFalse(vm.state.value.tripInfoShown)
    }

    // -------------------------------------------------------------------------
    // UnselectTrip
    // -------------------------------------------------------------------------

    @Test
    fun `UnselectTrip - clears trip and path data`() = runTest {
        tripsRepository.trips = ApiResult.Success(listOf(trip()))
        tripsRepository.getShapes = listOf(pathPoint())
        tripsRepository.getStops = listOf(stopWithTime())
        val vm = buildViewModel()
        vm.onAction(TripAction.SelectTrip(trip(), route(), stop()))
        testScheduler.advanceUntilIdle()

        vm.onAction(TripAction.UnselectTrip)

        assertNull(vm.state.value.trip)
        assertNull(vm.state.value.routeAssociated)
        assertEquals(emptyList<RoutePathPoint>(), vm.state.value.pathPoints)
        assertEquals(emptyList<StopWithLocationAndStopTime>(), vm.state.value.stops)
    }

    @Test
    fun `UnselectTrip - clears trip error`() = runTest {
        val vm = buildViewModel()
        // Manually force error state by selecting with empty shapes/stops
        tripsRepository.trips = ApiResult.Success(listOf(trip()))
        tripsRepository.getShapes = emptyList()
        tripsRepository.getStops = emptyList()
        vm.onAction(TripAction.SelectTrip(trip(), route(), stop()))
        testScheduler.advanceUntilIdle()
        assertTrue(vm.state.value.error.contains(Loadable.Trip()))

        vm.onAction(TripAction.UnselectTrip)

        assertFalse(vm.state.value.error.contains(Loadable.Trip()))
    }

    // -------------------------------------------------------------------------
    // SelectTrip — guard checks
    // -------------------------------------------------------------------------

    @Test(expected = IllegalStateException::class)
    fun `SelectTrip - trips in error state - throws IllegalStateException`() = runTest {
        tripsRepository.trips = ApiResult.Error("Failed")
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(trip(), route(), stop()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SelectTrip - trip not in dataset - throws IllegalArgumentException`() = runTest {
        tripsRepository.trips = ApiResult.Success(emptyList())  // trip not in list
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(trip(), route(), stop()))
    }

    @Test
    fun `SelectTrip - success - trip and route set in state`() = runTest {
        val testTrip = trip()
        val testRoute = route()
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = listOf(pathPoint())
        tripsRepository.getStops = listOf(stopWithTime())
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, testRoute, stop()))
        testScheduler.advanceUntilIdle()

        assertEquals(testTrip, vm.state.value.trip)
        assertEquals(testRoute, vm.state.value.routeAssociated)
    }

    @Test
    fun `SelectTrip - success - path points and stops set in state`() = runTest {
        val testTrip = trip()
        val shapes = listOf(pathPoint())
        val stops = listOf(stopWithTime())
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = shapes
        tripsRepository.getStops = stops
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, route(), stop()))
        testScheduler.advanceUntilIdle()

        assertEquals(shapes, vm.state.value.pathPoints)
        assertEquals(stops, vm.state.value.stops)
    }

    @Test
    fun `SelectTrip - success - tripInfoShown is true`() = runTest {
        val testTrip = trip()
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = listOf(pathPoint())
        tripsRepository.getStops = listOf(stopWithTime())
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, route(), stop()))
        testScheduler.advanceUntilIdle()

        assertTrue(vm.state.value.tripInfoShown)
    }

    @Test
    fun `SelectTrip - success - loading is cleared`() = runTest {
        val testTrip = trip()
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = listOf(pathPoint())
        tripsRepository.getStops = listOf(stopWithTime())
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, route(), stop()))
        testScheduler.advanceUntilIdle()

        assertFalse(vm.state.value.loading.contains(Loadable.Trip()))
    }

    @Test
    fun `SelectTrip - success - selectedThrough is set`() = runTest {
        val testTrip = trip()
        val testStop = stop()
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = listOf(pathPoint())
        tripsRepository.getStops = listOf(stopWithTime())
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, route(), testStop))
        testScheduler.advanceUntilIdle()

        assertEquals(testStop, vm.state.value.selectedThrough)
    }


    @Test
    fun `SelectTrip - empty shapes - sets error and clears trip`() = runTest {
        val testTrip = trip()
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = emptyList()
        tripsRepository.getStops = listOf(stopWithTime())
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, route(), stop()))
        testScheduler.advanceUntilIdle()

        assertTrue(vm.state.value.error.contains(Loadable.Trip()))
        assertNull(vm.state.value.trip)
        assertFalse(vm.state.value.tripInfoShown)
        assertFalse(vm.state.value.loading.contains(Loadable.Trip()))
    }

    @Test
    fun `SelectTrip - empty stops - sets error and clears trip`() = runTest {
        val testTrip = trip()
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = listOf(pathPoint())
        tripsRepository.getStops = emptyList()
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, route(), stop()))
        testScheduler.advanceUntilIdle()

        assertTrue(vm.state.value.error.contains(Loadable.Trip()))
        assertNull(vm.state.value.trip)
    }

    @Test
    fun `SelectTrip - success - no Favourite error`() = runTest {
        val testTrip = trip()
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = listOf(pathPoint())
        tripsRepository.getStops = listOf(stopWithTime())
        userRepository.getFavouritesResult = emptyList()
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, route(), stop()))
        testScheduler.advanceUntilIdle()

        assertFalse(vm.state.value.error.contains(Loadable.Favourites()))
    }

    @Test
    fun `SelectTrip - success - getFavourites success clears Favourites loading`() = runTest {
        val testTrip = trip()
        tripsRepository.trips = ApiResult.Success(listOf(testTrip))
        tripsRepository.getShapes = listOf(pathPoint())
        tripsRepository.getStops = listOf(stopWithTime())
        userRepository.getFavouritesResult = listOf(Favourite(route = route(), atMins = 600))
        val vm = buildViewModel()

        vm.onAction(TripAction.SelectTrip(testTrip, route(), stop()))
        testScheduler.advanceUntilIdle()

        assertFalse(vm.state.value.loading.contains(Loadable.Favourites()))
        assertFalse(vm.state.value.error.contains(Loadable.Favourites()))
    }


    @Test
    fun `ToggleFavourite - delegates to repository with correct args`() = runTest {
        val vm = buildViewModel()

        vm.onAction(TripAction.ToggleFavourite(routeId = "r1", atMins = 480))
        testScheduler.advanceUntilIdle()

        assertEquals("r1" to 480, userRepository.toggleFavouriteCalledWith)
    }
}