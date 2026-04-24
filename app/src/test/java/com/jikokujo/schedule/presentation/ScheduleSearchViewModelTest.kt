package com.jikokujo.schedule.presentation

import com.jikokujo.core.data.remote.ApiResult
import com.jikokujo.schedule.data.MockQueryablesRepository
import com.jikokujo.schedule.data.MockTripsRepository
import com.jikokujo.schedule.data.model.Location
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.data.model.StopWithLocationAndStopTime
import com.jikokujo.schedule.data.model.Trip
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
import org.junit.Assert.*
import kotlin.collections.listOf

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleSearchViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var queryablesRepository: MockQueryablesRepository
    private lateinit var tripsRepository: MockTripsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        queryablesRepository = MockQueryablesRepository()
        tripsRepository = MockTripsRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.buildViewModel(): ScheduleSearchViewModel {
        val vm = ScheduleSearchViewModel(
            queryableRepository = queryablesRepository,
            tripsRepository = tripsRepository,
            ioDispatcher = testDispatcher,
            defaultDispatcher = testDispatcher
        )
        testScheduler.advanceUntilIdle()
        return vm
    }

    private fun route(id: String = "1", shortName: String = "42") =
        Queryable.Route(id = id, shortName = shortName, type = 1, color = "FF0000")

    private fun stop(id: String = "s1", name: String = "Main St") =
        Queryable.Stop(ids = listOf(id), name = name)

    private fun stopWithTime(arrivalTime: Int = 600) = StopWithLocationAndStopTime(
        id = "s1",
        name = "TestStop",
        location = Location.Stop(lat = 47.0, lon = 19.0),
        arrivalTime = arrivalTime,
        order = 1
    )

    private fun trip(id: String = "t1", arrivalTime: Int = 600) = Trip(
        id = id,
        headSign = "Test Headsign",
        routeId = "r1",
        shapeId = "shape1",
        stops = listOf(stopWithTime(arrivalTime)),
        wheelchairAccessible = 0,
        bikesAllowed = 0,
        directionId = 0
    )

    @Test
    fun `init - queryables success - loading is cleared`() = runTest {
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(route()))
        val vm = buildViewModel()

        assertFalse(vm.state.value.loading.contains(Loadable.Queryables()))
    }

    @Test
    fun `init - queryables error - error loadable is set`() = runTest {
        queryablesRepository.queryablesRequestResult = ApiResult.Error("Test error")
        val vm = buildViewModel()

        assertTrue(vm.state.value.error.contains(Loadable.Queryables()))
        assertFalse(vm.state.value.loading.contains(Loadable.Queryables()))
    }

    @Test
    fun `RetryFetchInitialData - clears previous error and retries`() = runTest {
        queryablesRepository.queryablesRequestResult = ApiResult.Error("Test error")
        val vm = buildViewModel()
        assertTrue(vm.state.value.error.contains(Loadable.Queryables()))

        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(route()))
        vm.onAction(ScheduleAction.RetryFetchInitialData)
        testScheduler.advanceUntilIdle()

        assertFalse(vm.state.value.error.contains(Loadable.Queryables()))
    }


    @Test
    fun `ChangeDropDownState - expand with new dropdown - updates both fields`() = runTest {
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.ChangeDropDownState(true, DropDowns.TripSelection))

        assertTrue(vm.state.value.dropDownExpanded)
        assertEquals(DropDowns.TripSelection, vm.state.value.dropDownShown)
    }

    @Test
    fun `ChangeDropDownState - collapse - sets expanded to false`() = runTest {
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.ChangeDropDownState(false))

        assertFalse(vm.state.value.dropDownExpanded)
    }

    @Test
    fun `ChangeDropDownState - null dropDownShown - keeps previous value`() = runTest {
        val vm = buildViewModel()
        vm.onAction(ScheduleAction.ChangeDropDownState(true, DropDowns.TripSelection))

        vm.onAction(ScheduleAction.ChangeDropDownState(false, null))

        assertEquals(DropDowns.TripSelection, vm.state.value.dropDownShown)
    }


    @Test
    fun `ShowDialog - sets shown dialog`() = runTest {
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.ShowDialog(Dialogs.TimePicker))

        assertEquals(Dialogs.TimePicker, vm.state.value.shownDialog)
    }

    @Test
    fun `ShowDialog - null dismisses dialog`() = runTest {
        val vm = buildViewModel()
        vm.onAction(ScheduleAction.ShowDialog(Dialogs.TimePicker))

        vm.onAction(ScheduleAction.ShowDialog(null))

        assertNull(vm.state.value.shownDialog)
    }


    @Test
    fun `ChangeFromTime - updates hour and minute`() = runTest {
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.ChangeFromTime(14, 30))

        assertEquals(14, vm.state.value.tripTimeConstraint.hour)
        assertEquals(30, vm.state.value.tripTimeConstraint.minute)
    }

    @Test
    fun `ChangeFromTime - dismisses dialog`() = runTest {
        val vm = buildViewModel()
        vm.onAction(ScheduleAction.ShowDialog(Dialogs.TimePicker))

        vm.onAction(ScheduleAction.ChangeFromTime(14, 30))

        assertNull(vm.state.value.shownDialog)
    }

    @Test
    fun `ChangeFromDate - updates year month day`() = runTest {
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.ChangeFromDate(2025, 6, 15))

        assertEquals(2025, vm.state.value.tripTimeConstraint.year)
        assertEquals(6, vm.state.value.tripTimeConstraint.monthValue)
        assertEquals(15, vm.state.value.tripTimeConstraint.dayOfMonth)
    }

    @Test
    fun `ChangeFromDate - preserves existing time`() = runTest {
        val vm = buildViewModel()
        vm.onAction(ScheduleAction.ChangeFromTime(9, 45))

        vm.onAction(ScheduleAction.ChangeFromDate(2025, 6, 15))

        assertEquals(9, vm.state.value.tripTimeConstraint.hour)
        assertEquals(45, vm.state.value.tripTimeConstraint.minute)
    }

    @Test
    fun `ChangeFromDate - dismisses dialog`() = runTest {
        val vm = buildViewModel()
        vm.onAction(ScheduleAction.ShowDialog(Dialogs.DatePicker))

        vm.onAction(ScheduleAction.ChangeFromDate(2025, 6, 15))

        assertNull(vm.state.value.shownDialog)
    }


    @Test
    fun `SelectRoute - route in dataset - updates selectedQueryable`() = runTest {
        val testRoute = route()
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(testRoute))
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.SelectRoute(testRoute))

        assertEquals(testRoute, vm.state.value.selectedQueryable)
    }

    @Test
    fun `SelectRoute - route in dataset - sets search string to shortName`() = runTest {
        val testRoute = route(shortName = "42M")
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(testRoute))
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.SelectRoute(testRoute))

        assertEquals("42M", vm.state.value.searchString)
    }

    @Test
    fun `SelectRoute - route in dataset - collapses dropdown`() = runTest {
        val testRoute = route()
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(testRoute))
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.SelectRoute(testRoute))

        assertFalse(vm.state.value.dropDownExpanded)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SelectRoute - route not in dataset - throws IllegalArgumentException`() = runTest {
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(stop()))
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.SelectRoute(route()))
    }

    @Test(expected = IllegalStateException::class)
    fun `SelectRoute - queryables in error state - throws IllegalStateException`() = runTest {
        queryablesRepository.queryablesRequestResult = ApiResult.Error("Test error")
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.SelectRoute(route()))
    }

    @Test
    fun `SelectStop - stop in dataset - updates selectedQueryable`() = runTest {
        val testStop = stop()
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(testStop))
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.SelectStop(testStop))

        assertEquals(testStop, vm.state.value.selectedQueryable)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SelectStop - stop not in dataset - throws IllegalArgumentException`() = runTest {
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(route()))
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.SelectStop(stop()))
    }


    @Test
    fun `UnselectQueryable - clears selectedQueryable and searchString`() = runTest {
        val testRoute = route()
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(testRoute))
        val vm = buildViewModel()
        vm.onAction(ScheduleAction.SelectRoute(testRoute))

        vm.onAction(ScheduleAction.UnselectQueryable)

        assertNull(vm.state.value.selectedQueryable)
        assertEquals("", vm.state.value.searchString)
        assertEquals(emptyList<Queryable>(), vm.state.value.queryables)
    }

    @Test
    fun `UnselectTrip - clears selectedTrip and expands trip dropdown`() = runTest {
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.UnselectTrip)

        assertNull(vm.state.value.selectedTrip)
        assertTrue(vm.state.value.dropDownExpanded)
        assertEquals(DropDowns.TripSelection, vm.state.value.dropDownShown)
    }


    @Test(expected = IllegalStateException::class)
    fun `Search - no queryable selected - throws IllegalStateException`() = runTest {
        val vm = buildViewModel()

        vm.onAction(ScheduleAction.Search)
    }

    @Test
    fun `Search - success - trips are sorted by arrival time`() = runTest {
        val testRoute = route()
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(testRoute))
        val tripA = trip("t1")
        val tripB = trip("t2")
        tripsRepository.getTrips = ApiResult.Success(listOf(tripB, tripA))
        val vm = buildViewModel()
        vm.onAction(ScheduleAction.SelectRoute(testRoute))

        vm.onAction(ScheduleAction.Search)
        testScheduler.advanceUntilIdle()

        assertFalse(vm.state.value.loading.contains(Loadable.Trips()))
    }

    @Test
    fun `Search - error - error loadable is set`() = runTest {
        val testRoute = route()
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(testRoute))
        tripsRepository.getTrips = ApiResult.Error("Server error")
        val vm = buildViewModel()
        vm.onAction(ScheduleAction.SelectRoute(testRoute))

        vm.onAction(ScheduleAction.Search)
        testScheduler.advanceUntilIdle()

        assertTrue(vm.state.value.error.contains(Loadable.Trips()))
        assertFalse(vm.state.value.loading.contains(Loadable.Trips()))
    }


    @Test
    fun `getRoute - existing id - returns correct route`() = runTest {
        val testRoute = route(id = "99")
        queryablesRepository.queryablesRequestResult = ApiResult.Success(listOf(testRoute))
        val vm = buildViewModel()

        val result = vm.getRoute("99")

        assertEquals(testRoute, result)
    }

    @Test(expected = NoSuchElementException::class)
    fun `getRoute - unknown id - throws NoSuchElementException`() = runTest {
        queryablesRepository.queryablesRequestResult = ApiResult.Error("Test error")
        val vm = buildViewModel()

        vm.getRoute("does-not-exist")
    }
}