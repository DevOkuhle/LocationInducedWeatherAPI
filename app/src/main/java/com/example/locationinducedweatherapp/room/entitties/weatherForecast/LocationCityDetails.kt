package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LocationCityDetails (
    @Json(name = "id") var identifier: Long = -1,
    @Json(name = "name") var cityName: String = "",
    @Json(name = "coord") var coordinates: GridPoints = GridPoints(),
    var country: String = "",
    var population: Int = -1,
    var timezone: Int = -1,
    var sunrise: Long = -1,
    var sunset: Long = -1
)