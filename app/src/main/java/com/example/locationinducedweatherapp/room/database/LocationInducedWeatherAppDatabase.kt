package com.example.locationinducedweatherapp.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.locationinducedweatherapp.room.dao.LocationInducedWeatherDao
import com.example.locationinducedweatherapp.room.entitties.SavedFavourites

@Database(entities = [SavedFavourites::class], version = 1, exportSchema = true)
abstract class LocationInducedWeatherAppDatabase: RoomDatabase() {
    abstract fun locationInducedWeatherDao(): LocationInducedWeatherDao
}