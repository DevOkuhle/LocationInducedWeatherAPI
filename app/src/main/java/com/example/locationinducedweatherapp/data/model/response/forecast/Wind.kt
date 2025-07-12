package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Wind(
    @Json(name = "deg")
    var deg: Int = -1,
    @Json(name = "gust")
    var gust: Double = 0.0,
    @Json(name = "speed")
    var windSpeed: Double = 0.0
)