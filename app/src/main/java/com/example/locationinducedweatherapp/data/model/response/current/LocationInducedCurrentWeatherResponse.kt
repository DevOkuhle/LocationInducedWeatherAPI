package com.example.locationinducedweatherapp.data.model.response.current

import com.example.locationinducedweatherapp.data.model.response.forecast.Clouds
import com.example.locationinducedweatherapp.data.model.response.forecast.GridPoints
import com.example.locationinducedweatherapp.data.model.response.forecast.LocationCityDetails
import com.example.locationinducedweatherapp.data.model.response.forecast.Weather
import com.example.locationinducedweatherapp.data.model.response.forecast.WeatherConditionsNumericalSummary
import com.example.locationinducedweatherapp.data.model.response.forecast.Wind
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationInducedCurrentWeatherResponse (
    @Json(name = "coord") var locationCoordinated: GridPoints = GridPoints(),
    @Json(name = "weather") var weatherDescription: List<Weather> = emptyList(),
    @Json(name = "base") var baseStations: String = "",
    @Json(name = "main") var weatherConditionsNumericalSummary: WeatherConditionsNumericalSummary = WeatherConditionsNumericalSummary(),
    var visibility: Int = -1,
    @Json(name = "dt") val dateExecuted: Long = -1,
    var wind: Wind = Wind(),
    var rain: RainVolume = RainVolume(),
    var cloud: Clouds = Clouds(),
    @Json(name = "sys") var locationCityDetails: LocationCityDetails = LocationCityDetails(),
    var timezone: Int = -1,
    @Json(name = "id") var identifier: Long = -1,
    var name: String = "",
    @Json(name = "cod") var internalCodeReference: String = ""
)