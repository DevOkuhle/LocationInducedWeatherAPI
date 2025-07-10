package com.example.locationinducedweatherapp.data.model.response.forecast

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class WeatherConditionsOutline (
    @Json(name = "dt") var timeOfDataForecast: Long = -1,
    @Json(name = "main") var weatherConditionsNumericalSummary: WeatherConditionsNumericalSummary = WeatherConditionsNumericalSummary(),
    @Json(name = "weather") var weatherDescription: List<WeatherDescription> = emptyList(),
    var clouds: CloudPercentage = CloudPercentage(),
    var wind: WindDescription = WindDescription(),
    var visibility: Int = -1,
    @Json(name = "pop") var  precipitationPercentage: Double = 0.0,
    var rain: MeasureQuantityPerTheeHour = MeasureQuantityPerTheeHour(),
    @Json(name = "sys") var determinePartOfTheDay: DeterminePartOfTheDay = DeterminePartOfTheDay(),
    var snow: MeasureQuantityPerTheeHour = MeasureQuantityPerTheeHour(),
    @Json(name = "dt_txt") var weatherDate: String = ""
)