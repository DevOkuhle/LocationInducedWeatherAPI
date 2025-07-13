package com.example.locationinducedweatherapp.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.locationinducedweatherapp.room.dao.LocationInducedWeatherDao
import com.example.locationinducedweatherapp.room.entitties.SavedFavourites
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles

@Database(entities = [SavedFavourites::class, UserFavouriteLocationProfiles::class], version = 1, exportSchema = true)
abstract class LocationInducedWeatherAppDatabase: RoomDatabase() {
    abstract fun locationInducedWeatherDao(): LocationInducedWeatherDao
}