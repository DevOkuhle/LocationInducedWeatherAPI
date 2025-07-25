package com.example.locationinducedweatherapp.dependencyInjection

import android.app.Application
import androidx.room.Room
import com.example.locationinducedweatherapp.data.api.APIConfigurations
import com.example.locationinducedweatherapp.data.api.openWeather.OpenWeatherAPI
import com.example.locationinducedweatherapp.repository.room.LocationInducedWeatherRoomRepository
import com.example.locationinducedweatherapp.repository.room.LocationInducedWeatherRoomRepositoryImpl
import com.example.locationinducedweatherapp.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import com.example.locationinducedweatherapp.repository.weather.LocationInducedWeatherRepository
import com.example.locationinducedweatherapp.repository.weather.LocationInducedWeatherRepositoryImpl
import com.example.locationinducedweatherapp.room.dao.LocationInducedWeatherDao
import com.example.locationinducedweatherapp.room.database.LocationInducedWeatherAppDatabase
import com.example.locationinducedweatherapp.util.Constants.Companion.LOCATION_INDUCED_WEATHER_APP_DATABASE

@Module
@InstallIn(SingletonComponent::class)
object LocationInducedWeatherAppModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory = MoshiConverterFactory.create(moshi)

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesOpenWeatherAPI(moshiConverterFactory: MoshiConverterFactory, okHttpClient: OkHttpClient): OpenWeatherAPI = Retrofit.Builder()
        .baseUrl(Constants.OPEN_WEATHER_API_BASE_URL)
        .addConverterFactory(moshiConverterFactory)
        .client(okHttpClient)
        .build()
        .create(OpenWeatherAPI::class.java)

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesAPIConfigurations(): APIConfigurations = APIConfigurations()

    @Provides
    @Singleton
    fun providesCustomWeatherDatabase(appContext: Application): LocationInducedWeatherAppDatabase = Room.databaseBuilder(appContext.applicationContext, LocationInducedWeatherAppDatabase::class.java, LOCATION_INDUCED_WEATHER_APP_DATABASE).build()

    @Provides
    fun provideUserDao(locationInducedWeatherAppDatabase: LocationInducedWeatherAppDatabase): LocationInducedWeatherDao  = locationInducedWeatherAppDatabase.locationInducedWeatherDao()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesLocationInducedWeatherRepository(openWeatherAPI: OpenWeatherAPI, apiConfigurations: APIConfigurations, locationInducedWeatherDao: LocationInducedWeatherDao): LocationInducedWeatherRepository = LocationInducedWeatherRepositoryImpl(openWeatherAPI, apiConfigurations, locationInducedWeatherDao)

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesLocationInducedWeatherRoomRepository(locationInducedWeatherDao: LocationInducedWeatherDao): LocationInducedWeatherRoomRepository = LocationInducedWeatherRoomRepositoryImpl(locationInducedWeatherDao)
}