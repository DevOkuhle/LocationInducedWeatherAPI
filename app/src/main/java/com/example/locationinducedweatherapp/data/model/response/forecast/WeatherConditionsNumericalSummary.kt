package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherConditionsNumericalSummary(
    @Json(name = "feels_like")
    var feelsLikeTemperature: Double = 0.0,
    @Json(name = "grnd_level")
    var pressureAboveGroundLevel: Int = -1,
    @Json(name = "humidity")
    var humidity: Int = -1,
    @Json(name = "pressure")
    var pressure: Int = -1,
    @Json(name = "sea_level")
    var pressureAboveSeaLeve: Int = -1,
    @Json(name = "temp")
    var temperature: Double = 0.0,
    @Json(name = "temp_kf")
    var internalTemperatureQuantity: Double = 0.0,
    @Json(name = "temp_max")
    var maximumTemperature: Double = 0.0,
    @Json(name = "temp_min")
    var minimumTemperature: Double = 0.0
)