package com.example.locationinducedweatherapp.repository.room

import com.example.locationinducedweatherapp.room.entitties.SavedLocationWeatherForecast
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import kotlinx.coroutines.flow.Flow

interface LocationInducedWeatherRoomRepository {
    suspend fun addUserPreviousLocationsInducedWeather(savedLocationWeatherForecast: SavedLocationWeatherForecast)

    fun readUserPreviousLocationsInducedWeather(): Flow<List<SavedLocationWeatherForecast>>

    suspend fun addUserFavouriteLocationProfiles(userFavouriteLocationProfiles: UserFavouriteLocationProfiles)

    fun readUserFavouriteLocationProfiles(): Flow<List<UserFavouriteLocationProfiles>>

    fun getUserLocationsInducedWeatherByCoordinates(locationGridPoint: String): Flow<List<SavedLocationWeatherForecast>>
}