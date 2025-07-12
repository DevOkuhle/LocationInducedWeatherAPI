package com.example.locationinducedweatherapp.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.locationinducedweatherapp.room.entitties.SavedFavourites
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationInducedWeatherDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun getUserFavouriteLocations(savedFavourites: SavedFavourites)

    @Query("SELECT * FROM save_location_forecast ORDER BY identifier ASC")
    fun readUserFavouriteLocations(): Flow<List<SavedFavourites>>

    @Query("SELECT * FROM save_location_forecast WHERE locationGridPoint = :locationGridPoint")
    fun getUserFavouriteLocationsByCoordinates(locationGridPoint: String): Flow<List<SavedFavourites>>
}