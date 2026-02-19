package com.jikokujo.schedule.di

import com.jikokujo.BuildConfig
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.jikokujo.schedule.data.remote.QueryablesApi
import com.jikokujo.schedule.data.repository.QueryablesRepositoryImpl
import com.jikokujo.schedule.data.repository.QueryablesRepository
import com.jikokujo.schedule.data.repository.TripsRepository
import com.jikokujo.schedule.data.repository.TripsRepositoryImpl
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
    @Provides
    @Singleton
    fun provideQueryablesApi(): QueryablesApi {
        val gson = GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(QueryablesApi::class.java)
    }
    @Provides
    @Singleton
    fun provideQueryablesRepository(): QueryablesRepository {
        return QueryablesRepositoryImpl(provideQueryablesApi())
    }
    @Provides
    @Singleton
    fun provideTripsRepository(): TripsRepository {
        return TripsRepositoryImpl(provideQueryablesApi())
    }
}