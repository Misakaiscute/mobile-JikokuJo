package com.jikokuj.di

import com.jikokuj.data.remote.Api
import com.jikokuj.data.repository.QueryablesRepositoryImpl
import com.jikokuj.domain.repository.QueryableRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApi(): Api {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("http://localhost:8000/api/")
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .build()
            .create(Api::class.java)
    }
    @Provides
    @Singleton
    fun provideQueryablesRepository(): QueryableRepository {
        return QueryablesRepositoryImpl(provideApi())
    }
}