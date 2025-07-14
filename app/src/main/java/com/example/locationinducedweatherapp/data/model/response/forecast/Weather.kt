package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "description")
    var description: String = "",
    @Json(name = "icon")
    var icon: String = "",
    @Json(name = "id")
    var identifier: Int = -1,
    @Json(name = "main")
    var main: String = ""
)