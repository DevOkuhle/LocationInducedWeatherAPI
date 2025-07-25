package com.example.locationinducedweatherapp.repository.weather

import com.example.locationinducedweatherapp.data.api.APIResponseHandler
import com.example.locationinducedweatherapp.data.model.request.LocationWeatherAttributesRequest
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.LocationInducedForecastWeatherResponse
import com.example.locationinducedweatherapp.room.entitties.SavedLocationWeatherForecast
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import kotlinx.coroutines.flow.Flow

interface LocationInducedWeatherRepository {

    suspend fun getCurrentWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest): Flow<APIResponseHandler<LocationInducedCurrentWeatherResponse>>

    suspend fun getWeatherForecastInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest): Flow<APIResponseHandler<LocationInducedForecastWeatherResponse>>

    suspend fun addUserPreviousLocationsInducedWeather(savedLocationWeatherForecast: SavedLocationWeatherForecast)

    fun readUserPreviousLocationsInducedWeather(): Flow<List<SavedLocationWeatherForecast>>

    suspend fun addUserFavouriteLocationProfiles(userFavouriteLocationProfiles: UserFavouriteLocationProfiles)

    fun readUserFavouriteLocationProfiles(): Flow<List<UserFavouriteLocationProfiles>>

    fun getUserLocationsInducedWeatherByCoordinates(locationGridPoint: String): Flow<List<SavedLocationWeatherForecast>>
}