package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CloudPercentage(@Json(name = "clouds") var couldPercentage: Int = -1)