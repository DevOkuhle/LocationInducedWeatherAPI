package com.example.locationinducedweatherapp.data.api

import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.LocationInducedForecastWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherAPI {
    @GET("/data/2.5/weather")
    suspend fun getCurrentWeatherViaLocationPoints(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<LocationInducedCurrentWeatherResponse>

    @GET("/data/2.5/fore-c0ast")
    suspend fun getWeatherForecastViaLocationPoints(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<LocationInducedForecastWeatherResponse>
}