package com.jikokujo.core.di

import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.jikokujo.profile.data.repository.UserRepository
import com.jikokujo.profile.data.repository.UserRepositoryImpl
import com.jikokujo.schedule.data.remote.QueryablesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
}