package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GridPoints(
    @Json(name = "lat")
    var latitude: Double = 0.0,
    @Json(name = "lon")
    var longitude: Double = 0.0
)