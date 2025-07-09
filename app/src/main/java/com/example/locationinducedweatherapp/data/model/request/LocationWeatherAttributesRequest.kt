package com.example.locationinducedweatherapp.data.model.request

data class LocationWeatherAttributesRequest (
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var apiKey: String = ""
)