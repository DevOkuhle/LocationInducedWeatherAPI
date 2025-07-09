package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationInducedForecastWeatherResponse(
    @Json(name = "cod") var internalCodeReference: String = "",
    @Json(name = "message") var internalMessageID: Int = -1,
    @Json(name = "cnt") var timestampsCount: Int = -1,
    @Json(name = "list") var weatherConditionsOutlineList: List<WeatherConditionsOutline> = emptyList(),
    @Json(name = "city") var locationCityDetails: LocationCityDetails = LocationCityDetails()
)