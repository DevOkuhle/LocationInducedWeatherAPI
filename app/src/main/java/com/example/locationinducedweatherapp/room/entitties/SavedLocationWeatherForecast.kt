package com.example.locationinducedweatherapp.room.entitties

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save_location_forecast")
data class SavedLocationWeatherForecast (
    @PrimaryKey(autoGenerate = true) var identifier: Int = 0,
    var favouriteLocationName: String = "",
    var currentWeatherType: String = "",
    var currentMinimumTemperature: Double = 0.0,
    var currentMaximumTemperature: Double = 0.0,
    var currentTemperature: Double = 0.0,
    var averageTemperatures: String = "",
    var forecastDays: String = "",
    var iconResourceIdentifiers: String = "",
    var locationGridPoint: String = "",
    var cityName: String = "",
    var country: String = "",
    var weatherForecastTimeStamp: String = ""
)