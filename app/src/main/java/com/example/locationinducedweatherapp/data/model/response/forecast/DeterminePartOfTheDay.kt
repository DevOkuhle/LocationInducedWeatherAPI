package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeterminePartOfTheDay(@Json(name = "pod") var determinePartOfDay: String = "")