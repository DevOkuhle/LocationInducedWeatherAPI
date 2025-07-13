package com.example.locationinducedweatherapp.room.entitties

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_favourite_location_profiles")
data class UserFavouriteLocationProfiles (
    @PrimaryKey(autoGenerate = true) var identifier: Int = 0,
    var cityName: String = "",
    var country: String,
    var coordinates: String = "",
    var userGiveName: String = ""
)
