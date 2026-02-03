package com.jikokujo.profile.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.jikokujo.profile.data.remote.UserApi
import com.jikokujo.profile.data.repository.UserRepository
import com.jikokujo.profile.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    val Context.userAccessTokenDataStore: DataStore<Preferences> by preferencesDataStore(
        "user_access_token"
    )
    const val EMULATOR_DEV_LOCALHOST = "10.0.2.2"
    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        val gson = GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .create()

        return Retrofit.Builder()
            .baseUrl("http://$EMULATOR_DEV_LOCALHOST/backend-JikokuJo/public/index.php/api/")
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