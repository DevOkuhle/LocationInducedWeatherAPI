package com.example.locationinducedweatherapp.repository.room

import com.example.locationinducedweatherapp.room.dao.LocationInducedWeatherDao
import com.example.locationinducedweatherapp.room.entitties.SavedLocationWeatherForecast
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocationInducedWeatherRoomRepositoryImpl(private val locationInducedWeatherDao: LocationInducedWeatherDao): LocationInducedWeatherRoomRepository {
    override suspend fun addUserPreviousLocationsInducedWeather(savedLocationWeatherForecast: SavedLocationWeatherForecast) = locationInducedWeatherDao.addUserLocationsInducedWeather(savedLocationWeatherForecast)

    override fun readUserPreviousLocationsInducedWeather(): Flow<List<SavedLocationWeatherForecast>> = locationInducedWeatherDao.readUserLocationsInducedWeather()

    override suspend fun addUserFavouriteLocationProfiles(getUserFavouriteLocationProfiles: UserFavouriteLocationProfiles) = withContext(Dispatchers.IO) { locationInducedWeatherDao.addUserFavouriteLocationProfiles(getUserFavouriteLocationProfiles) }

    override fun readUserFavouriteLocationProfiles(): Flow<List<UserFavouriteLocationProfiles>> = locationInducedWeatherDao.readUserFavouriteLocationProfiles()

    override fun getUserLocationsInducedWeatherByCoordinates(locationGridPoint: String): Flow<List<SavedLocationWeatherForecast>> = locationInducedWeatherDao.getUserLocationsInducedWeatherByCoordinates(locationGridPoint)
}