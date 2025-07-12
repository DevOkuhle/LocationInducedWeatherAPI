package com.example.locationinducedweatherapp.ui.navigation

import com.example.locationinducedweatherapp.util.Constants.Companion.LOCATION_INDUCED_WEATHER_FAILURE
import com.example.locationinducedweatherapp.util.Constants.Companion.LOCATION_INDUCED_WEATHER_REPORT_SCREEN

sealed class LocationInducedWeatherNavigationScreen(val route: String) {
    data object LocationInducedWeatherReportScreen: LocationInducedWeatherNavigationScreen(route = LOCATION_INDUCED_WEATHER_REPORT_SCREEN)
    data object LocationInducedWeatherFailureScreen: LocationInducedWeatherNavigationScreen(route = LOCATION_INDUCED_WEATHER_FAILURE)
}