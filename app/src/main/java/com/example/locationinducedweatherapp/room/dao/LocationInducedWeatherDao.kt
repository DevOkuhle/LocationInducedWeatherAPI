package com.example.locationinducedweatherapp.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.locationinducedweatherapp.room.entitties.SavedLocationWeatherForecast
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationInducedWeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserLocationsInducedWeather(savedLocationWeatherForecast: SavedLocationWeatherForecast)

    @Query("SELECT * FROM save_location_forecast ORDER BY identifier ASC")
    fun readUserLocationsInducedWeather(): Flow<List<SavedLocationWeatherForecast>>

    @Query("SELECT * FROM save_location_forecast WHERE locationGridPoint = :locationGridPoint")
    fun getUserLocationsInducedWeatherByCoordinates(locationGridPoint: String): Flow<List<SavedLocationWeatherForecast>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserFavouriteLocationProfiles(userFavouriteLocationProfiles: UserFavouriteLocationProfiles)

    @Query("SELECT * FROM user_favourite_location_profiles ORDER BY identifier ASC")
    fun readUserFavouriteLocationProfiles(): Flow<List<UserFavouriteLocationProfiles>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_favourite_location_profiles WHERE coordinates = :locationGridPoint)")
    fun doesLocationAlreadyExist(locationGridPoint: String): Flow<Boolean>
}