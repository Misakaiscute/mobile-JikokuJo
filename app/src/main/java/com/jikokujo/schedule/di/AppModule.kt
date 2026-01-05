package com.jikokujo.schedule.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.jikokujo.schedule.data.remote.Api
import com.jikokujo.schedule.data.repository.QueryablesRepositoryImpl
import com.jikokujo.schedule.data.repository.QueryableRepository
import com.jikokujo.schedule.data.repository.QueryablesRepositoryTestImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
//        val client = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val response = chain.proceed(chain.request());
//                val peekedBody = response.peekBody(Long.MAX_VALUE).string()
//                val chunkSize = 3000
//                for (i in peekedBody.indices step chunkSize) {
//                    val end = (i + chunkSize).coerceAtMost(peekedBody.length)
//                    Log.d("OkHttp_Part_${i / chunkSize}", peekedBody.substring(i, end))
//                }
//                return@addInterceptor response
//            }
//            .build()

        val gson = GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .create()

        return Retrofit.Builder()
            .baseUrl("http://$EMULATOR_DEV_LOCALHOST:8000/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(client)
            .build()
            .create(Api::class.java)
    }
    @Provides
    @Singleton
    fun provideQueryablesRepository(): QueryableRepository {
        //return QueryablesRepositoryImpl(provideApi())
        return QueryablesRepositoryTestImpl()
    }
}