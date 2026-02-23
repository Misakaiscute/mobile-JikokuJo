package com.jikokujo.schedule.presentation

import com.jikokujo.schedule.data.MockQueryablesRepositoryImpl
import com.jikokujo.schedule.data.MockTripsRepositoryImpl
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.presentation.schedule.ScheduleAction
import com.jikokujo.schedule.presentation.schedule.DropDowns
import com.jikokujo.schedule.presentation.schedule.ScheduleSearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleSearchViewModelTest {
    private lateinit var viewModel: ScheduleSearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        val queryables = mutableListOf<Queryable>()
        (0..<(26)).forEach{ i ->
            queryables.add(Queryable.Route(
                id = i.toString(),
                shortName = (i + 65).toChar() + i.toString(),
                type = 1,
                color = i.toLong().toString()
            ))
            queryables.add(Queryable.Stop(
                ids = listOf(i.toString()),
                name = (i + 97).toChar() + i.toString()
            ))
        }
        val queryablesRepositoryTestImpl = MockQueryablesRepositoryImpl(
            queryablesIn = queryables,
        )
        val tripRepositoryTestImpl = MockTripsRepositoryImpl()

        this.viewModel = ScheduleSearchViewModel(
            queryableRepository = queryablesRepositoryTestImpl,
            tripsRepository = tripRepositoryTestImpl
        )
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `queryables fetched on VM initialization`(){
        assertNull(
            "The initial queryables must be fetched after VM creation",
            viewModel.state.value.error
        )
    }
    @Test
    fun `filtering empty string to anything`() = runTest {
        val searchString = "a"

        viewModel.onAction(ScheduleAction.ChangeSearch(searchString))
        advanceUntilIdle()

        val expected = listOf(
            Queryable.Route(id = "0", shortName = "A0", type = 1, color = 0.toLong().toString()),
            Queryable.Stop(ids = listOf("0"), name = "a0"),
        )
        assertTrue(
            "The state must contain the same (amount of) items as manually specified.\n" +
                    "Expected: ${expected.count()}\n" +
                    "Actual: ${viewModel.state.value.queryables.count()}",
            viewModel.state.value.queryables.count() == expected.count()
        )
        assertTrue(
            "Dropdown isn't expanded after searchString change",
            viewModel.state.value.dropDownExpanded
        )
        assertTrue(
            "Changing the search string must result in the dropdown staying on queryables",
            DropDowns.QueryableSelection == viewModel.state.value.dropDownShown
        )
        assertTrue(
            "Internal searchString state must be the same as the input",
            searchString == viewModel.state.value.searchString
        )
    }
    @Test
    fun `filtering anything to empty string`() = runTest {
        viewModel.onAction(ScheduleAction.ChangeSearch("b"))
        viewModel.onAction(ScheduleAction.ChangeSearch(""))
        advanceUntilIdle()
        assertTrue(
            "The state must contain no queryables at this point",
            viewModel.state.value.queryables.count() == 0
        )
        assertFalse(
            "Dropdown must not be expanded with no searchString given",
            viewModel.state.value.dropDownExpanded
        )
        assertTrue(
            "Changing the search string must result in the dropdown staying on queryables",
            DropDowns.QueryableSelection == viewModel.state.value.dropDownShown
        )
        assertTrue(
            "Internal searchString state must be the same as the input",
            "" == viewModel.state.value.searchString
        )
    }
    @Test
    fun `select EXISTING stop`(){
        val stop = Queryable.Stop(listOf("0"), "a0")
        runBlocking {
            viewModel.onAction(ScheduleAction.SelectStop(stop))
        }
        assertTrue(
            "The state must contain the correct stop",
            (viewModel.state.value.selectedQueryable as Queryable.Stop).ids == stop.ids
        )
        assertFalse(
            "Dropdown must be not expanded after selecting a stop",
            viewModel.state.value.dropDownExpanded
        )
        assertTrue(
            "Selecting a stop must result in the searchString switching to the name of the stop",
            stop.name == viewModel.state.value.searchString
        )
    }
    @Test
    fun `select NON-EXISTENT stop`(){
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                viewModel.onAction(ScheduleAction.SelectStop(
                    Queryable.Stop(ids = listOf("asddd"), name = "mindegyezbarmilehetlol")
                ))
            }
        }
        assertTrue(
            "Function must throw IllegalStateException when a non-existent stop is selected",
            exception is IllegalArgumentException
        )
    }
    @Test
    fun `select EXISTING route`(){
        val route = Queryable.Route(id = "0", shortName = "A0", type = 1, color = 0.toLong().toString())
        runBlocking {
            viewModel.onAction(ScheduleAction.SelectRoute(route))
        }
        assertTrue(
            "The state must contain the selected queryable",
            (viewModel.state.value.selectedQueryable as Queryable.Route).id == route.id
        )
        assertFalse(
            "Dropdown must be not expanded after selecting a stop",
            viewModel.state.value.dropDownExpanded
        )
        assertTrue(
            "Selecting a stop must result in the searchString switching to the name of the route",
            route.shortName == viewModel.state.value.searchString
        )
    }
    @Test
    fun `select NON-EXISTENT route`(){
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                viewModel.onAction(ScheduleAction.SelectRoute(
                    Queryable.Route(id = "asddd", shortName = "mindegyezbarmilehetlol", type = 1, color = "1")
                ))
            }
        }
        assertTrue(
            "Function must throw IllegalArgumentException when a non-existent route is selected",
            exception is IllegalArgumentException
        )
    }
    @Test
    fun `search with no queryable selected`(){
        val exception = assertThrows(IllegalStateException::class.java){
            runBlocking { viewModel.onAction(ScheduleAction.Search) }
        }
        assertTrue(
            "Function must throw IllegalStateException when no stop or route is selected",
            exception is IllegalStateException
        )
    }
    @Test
    fun `search with stop selected`(){
        runBlocking {
            viewModel.onAction(ScheduleAction.SelectStop(
                Queryable.Stop(ids = listOf("4"), name = "e4")
            ))
            viewModel.onAction(ScheduleAction.Search)
        }
        assertTrue(
            "Dropdown must be expanded after selecting a stop",
            viewModel.state.value.dropDownExpanded
        )
        assertTrue(
            "Selecting a stop must result in the dropdown switching RouteSelection",
            DropDowns.TripSelection == viewModel.state.value.dropDownShown
        )
        assertFalse(
            "Search must have finished loading",
            viewModel.state.value.isLoading
        )
    }
    @Test
    fun `search with route selected`(){
        runBlocking {
            viewModel.onAction(ScheduleAction.SelectRoute(
                Queryable.Route(id = "0", shortName = "A0", type = 1, color = 1.toLong().toString())
            ))
            viewModel.onAction(ScheduleAction.Search)
        }
        assertTrue(
            "Dropdown must be expanded after selecting a route",
            viewModel.state.value.dropDownExpanded
        )
        assertTrue(
            "Selecting a route must result in the dropdown switching TripSelection",
            DropDowns.TripSelection == viewModel.state.value.dropDownShown
        )
        assertFalse(
            "Search must have finished loading",
            viewModel.state.value.isLoading
        )
    }
}