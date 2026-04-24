package com.jikokujo.profile.presentation

import com.jikokujo.core.data.model.Favourite
import com.jikokujo.core.data.model.User
import com.jikokujo.core.data.MockUserRepository
import com.jikokujo.schedule.data.model.Queryable
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userRepository: MockUserRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = MockUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.buildViewModel(): ProfileViewModel {
        val vm = ProfileViewModel(
            userRepository,
            testDispatcher
        )
        testScheduler.advanceUntilIdle()
        return vm
    }


    @Test
    fun `init - auth succeeds - isLoggedIn is true and loading is cleared`() = runTest {
        userRepository.authenticated = true
        val vm = buildViewModel()

        assertTrue(vm.state.value.isLoggedIn)
        assertFalse(vm.state.value.error.contains(Loadable.Authentication()))
        assertFalse(vm.state.value.loading.contains(Loadable.Authentication()))
    }

    @Test
    fun `init - auth fails - isLoggedIn is false and error is set`() = runTest {
        userRepository.authenticated = false
        val vm = buildViewModel()

        assertFalse(vm.state.value.isLoggedIn)
        assertFalse(vm.state.value.loading.contains(Loadable.Authentication()))
        assertTrue(vm.state.value.error.contains(Loadable.Authentication()))
    }

    @Test
    fun `AttemptAuth - retrying after failure clears previous error`() = runTest {
        userRepository.authenticated = false
        val vm = buildViewModel()
        assertTrue(vm.state.value.error.contains(Loadable.Authentication()))

        userRepository.authenticated = true
        vm.onAction(ProfileAction.AttemptAuth)
        advanceUntilIdle()

        assertFalse(vm.state.value.error.contains(Loadable.Authentication()))
        assertTrue(vm.state.value.isLoggedIn)
    }


    @Test
    fun `FetchUser - success - user is set in state`() = runTest {
        userRepository.authenticated = true
        val fakeUser = User(id = 1, firstName = "John", lastName = "Doe", email = "john@example.com")
        userRepository.getUserResult = fakeUser
        val vm = buildViewModel()

        vm.onAction(ProfileAction.FetchUser)
        advanceUntilIdle()

        assertEquals(fakeUser, vm.state.value.user)
        assertFalse(vm.state.value.loading.contains(Loadable.User()))
    }

    @Test
    fun `FetchUser - error - user remains null and error is set`() = runTest {
        userRepository.authenticated = true
        userRepository.getUserResult = null
        val vm = buildViewModel()

        vm.onAction(ProfileAction.FetchUser)
        advanceUntilIdle()

        assertNull(vm.state.value.user)
        assertTrue(vm.state.value.error.contains(Loadable.User()))
    }


    @Test
    fun `FetchFavourites - error - error loadable is set`() = runTest {
        userRepository.authenticated = true
        userRepository.getFavouritesResult = null
        val vm = buildViewModel()

        vm.onAction(ProfileAction.FetchFavourites)
        advanceUntilIdle()

        assertTrue(vm.state.value.error.contains(Loadable.Favourites()))
    }

    @Test
    fun `FetchFavourites - success - loading is cleared and no error`() = runTest {
        userRepository.authenticated = true
        userRepository.getFavouritesResult = listOf(
            Favourite(route = Queryable.Route(id = "42", shortName = "Testing", type = 1, color = "000000"), atMins = 480)
        )
        val vm = buildViewModel()

        vm.onAction(ProfileAction.FetchFavourites)
        advanceUntilIdle()

        assertFalse(vm.state.value.loading.contains(Loadable.Favourites()))
        assertFalse(vm.state.value.error.contains(Loadable.Favourites()))
    }

    @Test
    fun `FetchFavourites - retrying after error clears previous error`() = runTest {
        userRepository.authenticated = true
        userRepository.getFavouritesResult = null
        val vm = buildViewModel()

        vm.onAction(ProfileAction.FetchFavourites)
        advanceUntilIdle()
        assertTrue(vm.state.value.error.contains(Loadable.Favourites()))

        userRepository.getFavouritesResult = listOf(
            Favourite(route = Queryable.Route(id = "42", shortName = "Testing", type = 1, color = "000000"), atMins = 480)
        )
        vm.onAction(ProfileAction.FetchFavourites)
        advanceUntilIdle()

        assertFalse(vm.state.value.error.contains(Loadable.Favourites()))
    }


    @Test
    fun `favourites flow emission updates state`() = runTest {
        userRepository.authenticated = true
        val vm = buildViewModel()
        val newFavourites = listOf(
            Favourite(route = Queryable.Route(id = "42", shortName = "Testing", type = 1, color = "000000"), atMins = 480)
        )

        userRepository.favourites.update { newFavourites }
        advanceUntilIdle()

        assertEquals(newFavourites, vm.state.value.favourites)
    }

    @Test
    fun `favourites flow emitting empty list clears favourites in state`() = runTest(UnconfinedTestDispatcher()) {
        userRepository.authenticated = true
        val vm = buildViewModel()
        userRepository.favourites.emit(listOf(
            Favourite(route = Queryable.Route(id = "1", shortName = "Testing", type = 1, color = "000000"), atMins = 100)
        ))
        advanceUntilIdle()

        userRepository.favourites.emit(emptyList())
        advanceUntilIdle()

        assertEquals(emptyList<Favourite>(), vm.state.value.favourites)
    }


    @Test
    fun `ToggleFavourite - delegates to repository with correct args`() = runTest {
        userRepository.authenticated = true
        val vm = buildViewModel()

        vm.onAction(ProfileAction.ToggleFavourite(routeId = "99", atMins = 720))
        advanceUntilIdle()

        assertEquals("99" to 720, userRepository.toggleFavouriteCalledWith)
    }


    @Test
    fun `LogOut - sets isLoggedIn to false`() = runTest {
        userRepository.authenticated = true
        val vm = buildViewModel()
        assertTrue(vm.state.value.isLoggedIn)

        vm.onAction(ProfileAction.LogOut)
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoggedIn)
    }

    @Test
    fun `LogOut - calls repository logout`() = runTest {
        userRepository.authenticated = true
        val vm = buildViewModel()

        vm.onAction(ProfileAction.LogOut)
        advanceUntilIdle()

        assertTrue(userRepository.logoutCalled)
    }
}