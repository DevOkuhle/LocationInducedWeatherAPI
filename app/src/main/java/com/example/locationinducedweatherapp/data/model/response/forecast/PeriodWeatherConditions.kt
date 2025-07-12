package com.example.locationinducedweatherapp.data.model.response.forecast


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PeriodWeatherConditions(
    @Json(name = "clouds")
    var clouds: Clouds,
    @Json(name = "dt")
    var timeOfDataForecast: Int,
    @Json(name = "dt_txt")
    var weatherDate: String,
    @Json(name = "main")
    var weatherConditionsNumericalSummary: WeatherConditionsNumericalSummary,
    @Json(name = "pop")
    var precipitationPercentage: Double,
    @Json(name = "rain")
    var rain: Rain? = null,
    @Json(name = "sys")
    var determinePartOfTheDay: DeterminePartOfTheDay,
    @Json(name = "visibility")
    var visibility: Int,
    @Json(name = "weather")
    var weather: List<Weather>,
    @Json(name = "wind")
    var wind: Wind
)