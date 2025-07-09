package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherDescription (
    @Json(name = "id") var weatherConditionID: Int = -1,
    @Json(name = "main") var weatherEventOccurrence: String = "",
    var description: String = "",
    var icon: String = ""
)