package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class WeatherConditionsNumericalSummary (
    @Json(name = "temp") var temperature: Int = -1,
    @Json(name = "feels_like") var feelsLikeTemperature: Int = -1,
    @Json(name = "temp_min") var minimumTemperature: Int = -1,
    @Json(name = "temp_max")var maximumTemperature: Int = -1,
    var pressure: Long = -1,
    @Json(name = "sea_level") var pressureAboveSeaLeve: Int = 1015,
    @Json(name = "grnd_level") var pressureAboveGroundLevel: Int = -1,
    val humidity: Double = 0.0,
    @Json(name = "temp_kf") var internalTemperatureQuantity: Double = 0.0
)