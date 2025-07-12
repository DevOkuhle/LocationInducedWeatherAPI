package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationCityDetails(
    @Json(name = "coord")
    var coordinates: GridPoints = GridPoints(),
    @Json(name = "country")
    var country: String = "",
    @Json(name = "id")
    var identifier: Int = -1,
    @Json(name = "name")
    var cityName: String = "",
    @Json(name = "population")
    var population: Int = -1,
    @Json(name = "sunrise")
    var sunrise: Int = -1,
    @Json(name = "sunset")
    var sunset: Int = -1,
    @Json(name = "timezone")
    var timezone: Int = -1
)