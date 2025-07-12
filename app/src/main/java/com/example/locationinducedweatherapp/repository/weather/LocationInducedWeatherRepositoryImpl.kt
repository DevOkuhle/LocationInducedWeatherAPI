package com.example.locationinducedweatherapp.repository.weather

import com.example.locationinducedweatherapp.data.api.APIConfigurations
import com.example.locationinducedweatherapp.data.api.APIResponseHandler
import com.example.locationinducedweatherapp.data.api.openWeather.OpenWeatherAPI
import com.example.locationinducedweatherapp.data.model.request.LocationWeatherAttributesRequest
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.room.dao.LocationInducedWeatherDao
import com.example.locationinducedweatherapp.data.model.response.forecast.LocationInducedForecastWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationInducedWeatherRepositoryImpl @Inject constructor(private val openWeatherAPI: OpenWeatherAPI, private val apiConfigurations: APIConfigurations, locationInducedWeatherDao: LocationInducedWeatherDao): LocationInducedWeatherRepository {

    override suspend fun getCurrentWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest): Flow<APIResponseHandler<LocationInducedCurrentWeatherResponse>> =
        withContext(Dispatchers.IO) {
            apiConfigurations.populateResponseFromWeatherAPI {
                openWeatherAPI.getCurrentWeatherViaLocationPoints(
                    latitude = locationWeatherAttributesRequest.latitude,
                    longitude = locationWeatherAttributesRequest.longitude,
                    apiKey = locationWeatherAttributesRequest.apiKey
                )
            }
        }

    override suspend fun getWeatherForecastInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest): Flow<APIResponseHandler<LocationInducedForecastWeatherResponse>> =
        withContext(Dispatchers.IO) {
            apiConfigurations.populateResponseFromWeatherAPI {
                openWeatherAPI.getWeatherForecastViaLocationPoints(
                    latitude = locationWeatherAttributesRequest.latitude,
                    longitude = locationWeatherAttributesRequest.longitude,
                    apiKey = locationWeatherAttributesRequest.apiKey
                )
            }
        }
}