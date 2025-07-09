package com.example.locationinducedweatherapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationinducedweatherapp.data.api.APIResponseHandler
import com.example.locationinducedweatherapp.data.model.request.LocationWeatherAttributesRequest
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.LocationInducedForecastWeatherResponse
import com.example.locationinducedweatherapp.repository.weather.LocationInducedWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationInducedViewModel @Inject constructor(private val locationInducedWeatherRepository: LocationInducedWeatherRepository): ViewModel() {

    private val _currentLocationWeatherInformationMutableStateFlow = MutableStateFlow(LocationInducedCurrentWeatherResponse())
    val weatherForecastByGridPointsMutableStateFlow = _currentLocationWeatherInformationMutableStateFlow.asStateFlow()

    private val _locationWeatherForecastMutableStateFlow = MutableStateFlow(LocationInducedForecastWeatherResponse())
    val weatherForecastByGridPointsHourlyMutableStateFlow = _locationWeatherForecastMutableStateFlow.asStateFlow()

    private val _failureResponseMutableStateFlow = MutableStateFlow("")
    val failureResponseMutableStateFlow = _failureResponseMutableStateFlow.asStateFlow()

    var isWeatherAPISuccessful: Boolean? = null

    fun getCurrentWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) {
        invokeAPICallsAndPopulateStateFlows(_currentLocationWeatherInformationMutableStateFlow) {
            locationInducedWeatherRepository.getCurrentWeatherInformation(locationWeatherAttributesRequest)
        }
    }

    fun getLocationWeatherForecast(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) {
        invokeAPICallsAndPopulateStateFlows(_locationWeatherForecastMutableStateFlow) {
            locationInducedWeatherRepository.getWeatherForecastInformation(locationWeatherAttributesRequest)
        }
    }

    private fun <T> invokeAPICallsAndPopulateStateFlows(locationWeatherMutableStateFlow: MutableStateFlow<T>, apiWeatherInvocation: suspend() -> Flow<APIResponseHandler<T>>) {
        viewModelScope.launch {
            apiWeatherInvocation().collectLatest { response ->
                when (response) {
                    is APIResponseHandler.Failure -> {
                        isWeatherAPISuccessful = false
                        response.failureMessage?.let { failureMessage ->
                            _failureResponseMutableStateFlow.update { failureMessage }
                        }
                    }

                    is APIResponseHandler.Success -> {
                        isWeatherAPISuccessful = true
                        response.successResponse?.let { weatherResponse ->
                            locationWeatherMutableStateFlow.update { weatherResponse }
                        }
                    }
                }
            }
        }
    }
}