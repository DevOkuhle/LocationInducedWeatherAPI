package com.example.locationinducedweatherapp.data.model.response.current

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RainVolume (@Json(name = "1h") var rainVolumeHourly: Double = 0.0)