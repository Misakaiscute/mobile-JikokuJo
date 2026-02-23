package com.jikokujo.profile.presentation

import com.jikokujo.profile.data.MockUserRepositoryImpl
import com.jikokujo.profile.presentation.auth.RegisterViewModel
import org.junit.Before
import org.junit.Test

class RegisterViewModelTest {
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        this.viewModel = RegisterViewModel(
            userRepository = MockUserRepositoryImpl()
        )
    }
    @Test
    fun `example test`(){

    }
}