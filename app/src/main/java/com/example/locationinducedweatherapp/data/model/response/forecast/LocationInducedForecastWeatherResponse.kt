package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationInducedForecastWeatherResponse(
    @Json(name = "locationCityDetails")
    var locationCityDetails: LocationCityDetails = LocationCityDetails(),
    @Json(name = "cnt")
    var internalData: Int = -1,
    @Json(name = "cod")
    var internalCode: String = "",
    @Json(name = "list")
    var periodWeatherConditions: List<PeriodWeatherConditions> = emptyList(),
    @Json(name = "message")
    var message: Int = -1
)