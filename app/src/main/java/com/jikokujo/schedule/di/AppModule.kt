package com.jikokujo.schedule.di

import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.jikokujo.BuildConfig
import com.jikokujo.core.utils.EmulatorDetector
import com.jikokujo.schedule.data.remote.QueryablesApi
import com.jikokujo.schedule.data.repository.QueryablesRepositoryImpl
import com.jikokujo.schedule.data.repository.QueryablesRepository
import com.jikokujo.schedule.data.repository.TripsRepository
import com.jikokujo.schedule.data.repository.TripsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(if (EmulatorDetector.isEmulator) BuildConfig.EMULATED_DEVICE_API else BuildConfig.PHYSICAL_DEVICE_API)
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