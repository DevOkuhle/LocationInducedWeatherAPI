package com.example.locationinducedweatherapp.repository.weather

import com.example.locationinducedweatherapp.data.api.APIResponseHandler
import com.example.locationinducedweatherapp.data.model.request.LocationWeatherAttributesRequest
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.LocationInducedForecastWeatherResponse
import com.example.locationinducedweatherapp.room.entitties.SavedFavourites
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import kotlinx.coroutines.flow.Flow

interface LocationInducedWeatherRepository {

    suspend fun getCurrentWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest): Flow<APIResponseHandler<LocationInducedCurrentWeatherResponse>>

    suspend fun getWeatherForecastInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest): Flow<APIResponseHandler<LocationInducedForecastWeatherResponse>>

    suspend fun addUserFavouriteLocations(savedFavourites: SavedFavourites)

    fun readUserFavouriteLocations(): Flow<List<SavedFavourites>>

    fun getUserFavouriteLocationsByCoordinates(locationGridPoint: String): Flow<List<SavedFavourites>>

    suspend fun addUserFavouriteLocationProfiles(userFavouriteLocationProfiles: UserFavouriteLocationProfiles)

    fun readUserFavouriteLocationProfiles(): Flow<List<UserFavouriteLocationProfiles>>

    fun getUserFavouriteLocationProfilesByCoordinates(locationGridPoint: String): Flow<UserFavouriteLocationProfiles>

    fun doesLocationAlreadyExist(locationGridPoint: String): Flow<Boolean>
}