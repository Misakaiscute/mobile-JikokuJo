package com.jikokujo.schedule.presentation

import com.jikokujo.schedule.data.QueryablesRepositoryTestImpl
import com.jikokujo.schedule.data.RouteResultRepositoryTestImpl
import com.jikokujo.schedule.data.model.Queryable
import com.jikokujo.schedule.presentation.schedule.Action
import com.jikokujo.schedule.presentation.schedule.DropDowns
import com.jikokujo.schedule.presentation.schedule.ScheduleSearchViewModel
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ScheduleSearchViewModelTest {
    private lateinit var viewModel: ScheduleSearchViewModel

    @Before
    fun setUp() {
        val queryables = mutableListOf<Queryable>()
        val routesForStop = mutableListOf<Queryable.Route>()
        (0..<(3 * 26)).forEachIndexed { index, i ->
            queryables.add(Queryable.Route(
                id = index.toString(),
                name = (i % 27 + 65).toChar() + index.toString(),
                type = 1,
                color = index.toLong().toString()
            ))
            queryables.add(Queryable.Stop(
                id = index.toString(),
                name = (i % 27 + 97).toChar() + index.toString()
            ))
        }
        ('a'..'z').forEachIndexed {  index, ch ->
            routesForStop.add(Queryable.Route(
                id = index.toString(),
                name = ch + index.toString(),
                type = 1,
                color = index.toLong().toString()
            ))
        }
        val queryablesRepositoryTestImpl = QueryablesRepositoryTestImpl(
            queryablesIn = queryables,
            routesForStopIn = routesForStop
        )
        val routeResultRepositoryTestImpl = RouteResultRepositoryTestImpl()
        this.viewModel = ScheduleSearchViewModel(
            queryableRepository = queryablesRepositoryTestImpl,
            routeResultRepository = routeResultRepositoryTestImpl
        )
    }
    @Test
    fun `queryables fetched on VM initialization`(){
        assertNull(
            "The initial queryables must be fetched after VM creation",
            viewModel.state.value.error
        )
    }
    @Test
    fun `filtering empty string to anything`(){
        val searchString = "a"
        viewModel.onAction(Action.ChangeSearch(searchString))
        val expected = listOf(
            Queryable.Route(id = "0", name = "A0", type = 1, color = 0.toLong().toString()),
            Queryable.Route(id = "26", name = "A26", type = 1, color = 26.toLong().toString()),
            Queryable.Route(id = "52", name = "A52", type = 1, color = 52.toLong().toString()),
            Queryable.Stop(id = "0", name = "a0"),
            Queryable.Stop(id = "26", name = "a26"),
            Queryable.Stop(id = "52", name = "a52")
        )
        assertTrue(
            "The state must contain the same (amount of) items as manually specified",
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
    fun `filtering anything to empty string`(){
        viewModel.onAction(Action.ChangeSearch(""))
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
        val stop = Queryable.Stop("26", "a26")
        viewModel.onAction(Action.SelectStop(stop))
        assertTrue(
            "The state must contain the correct stop",
            (viewModel.state.value.selectedQueryable as Queryable.Stop).id == stop.id
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
            viewModel.onAction(Action.SelectStop(
                Queryable.Stop(id = "asddd", name = "mindegyezbarmilehetlol")
            ))
        }
        assertTrue(
            "Function must throw IllegalStateException when a non-existent stop is selected",
            exception is IllegalArgumentException
        )
    }
    @Test
    fun `select EXISTING route`(){
        val route = Queryable.Route(id = "27", name = "B27", type = 1, color = 27.toLong().toString())
        viewModel.onAction(Action.SelectRoute(route))
        assertTrue(
            "The state must contain no queryables at this point",
            (viewModel.state.value.selectedQueryable as Queryable.Route).id == route.id
        )
        assertFalse(
            "Dropdown must be not expanded after selecting a stop",
            viewModel.state.value.dropDownExpanded
        )
        assertTrue(
            "Selecting a stop must result in the searchString switching to the name of the route",
            route.name == viewModel.state.value.searchString
        )
    }
    @Test
    fun `select NON-EXISTENT route`(){
        val exception = assertThrows(IllegalArgumentException::class.java) {
            viewModel.onAction(Action.SelectRoute(
                Queryable.Route(id = "asddd", name = "mindegyezbarmilehetlol", type = 1, color = "1")
            ))
        }
        assertTrue(
            "Function must throw IllegalArgumentException when a non-existent route is selected",
            exception is IllegalArgumentException
        )
    }
    @Test
    fun `search with no queryable selected`(){
        val exception = assertThrows(IllegalStateException::class.java){
            viewModel.onAction(Action.Search)
        }
        assertTrue(
            "Function must throw IllegalStateException when no stop or route is selected",
            exception is IllegalStateException
        )
    }
    @Test
    fun `search with stop selected`(){
        viewModel.onAction(Action.SelectStop(
            Queryable.Stop(id = "4", name = "e4")
        ))
        viewModel.onAction(Action.Search)
        assertTrue(
            "Dropdown must be expanded after selecting a stop",
            viewModel.state.value.dropDownExpanded
        )
        assertTrue(
            "Selecting a stop must result in the dropdown switching RouteSelection",
            DropDowns.RouteSelection == viewModel.state.value.dropDownShown
        )
        assertFalse(
            "Search must have finished loading",
            viewModel.state.value.isLoading
        )
    }
    @Test
    fun `search with route selected`(){
        viewModel.onAction(Action.SelectRoute(
            Queryable.Route(id = "0", name = "A0", type = 1, color = 1.toLong().toString())
        ))
        viewModel.onAction(Action.Search)
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