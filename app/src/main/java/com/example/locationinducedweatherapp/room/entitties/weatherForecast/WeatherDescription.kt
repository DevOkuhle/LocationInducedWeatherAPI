package com.example.locationinducedweatherapp.room.entitties.weatherForecast

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_description")
data class WeatherDescription (
    var weatherConditionID: Int = -1,
    var weatherEventOccurrence: String = "",
    var description: String = "",
    var icon: String = ""
)