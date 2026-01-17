package com.jikokujo.schedule.di

import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.jikokujo.schedule.data.remote.Api
import com.jikokujo.schedule.data.repository.QueryablesRepositoryImpl
import com.jikokujo.schedule.data.repository.QueryableRepository
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
    const val EMULATOR_DEV_LOCALHOST = "10.0.2.2"
    @Provides
    @Singleton
    fun provideApi(): Api {
        val gson = GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .create()

        return Retrofit.Builder()
            .baseUrl("http://$EMULATOR_DEV_LOCALHOST/backend-JikokuJo/better_menetrendek_backend/public/index.php/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(Api::class.java)
    }
    @Provides
    @Singleton
    fun provideQueryablesRepository(): QueryableRepository {
        return QueryablesRepositoryImpl(provideApi())
    }
    @Provides
    @Singleton
    fun provideRouteResultRepository(): TripsRepository{
        return TripsRepositoryImpl(provideApi())
    }
}