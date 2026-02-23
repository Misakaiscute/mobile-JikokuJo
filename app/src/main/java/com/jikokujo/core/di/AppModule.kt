package com.jikokujo.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.jikokujo.BuildConfig
import com.jikokujo.core.utils.EmulatorDetector
import com.jikokujo.core.data.remote.UserApi
import com.jikokujo.core.data.repository.UserRepository
import com.jikokujo.core.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    val Context.userAccessTokenDataStore: DataStore<Preferences> by preferencesDataStore(
        "user_access_token"
    )
    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
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
            .create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(@ApplicationContext context: Context): UserRepository {
        return UserRepositoryImpl(
            api = provideUserApi(),
            dataStore = context.userAccessTokenDataStore
        )
    }
}