package com.example.locationinducedweatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.locationinducedweatherapp.data.model.PackageLocationInducedWeatherViewModels
import com.example.locationinducedweatherapp.ui.LocationInducedWeatherFailure
import com.example.locationinducedweatherapp.ui.LocationInducedWeatherReport
import com.example.locationinducedweatherapp.ui.ViewAllFavouriteLocationsInGoogleMaps
import com.example.locationinducedweatherapp.ui.ViewFavouriteLocationProfiles
import com.example.locationinducedweatherapp.ui.SearchForLocationInAutoComplete

@Composable
fun LocationInducedWeatherNavigationGraph(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels) {
    NavHost(navController = locationInducedViewModel.navigationController, startDestination = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route) {
        composable(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route) {
            LocationInducedWeatherReport(packageLocationInducedWeatherViewModels)
        }

        composable(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherFailureScreen.route) {
            LocationInducedWeatherFailure(locationInducedViewModel)
        }

        composable(route = LocationInducedWeatherNavigationScreen.ViewFavouriteLocationProfilesScreen.route) {
            ViewFavouriteLocationProfiles(packageLocationInducedWeatherViewModels)
        }

        composable(route = LocationInducedWeatherNavigationScreen.ViewAllFavouriteLocationsInGoogleMapsScreen.route) {
            ViewAllFavouriteLocationsInGoogleMaps(packageLocationInducedWeatherViewModels)
        }

        composable(route = LocationInducedWeatherNavigationScreen.ViewUserGooglePlacesScreen.route) {
            SearchForLocationInAutoComplete(locationInducedViewModel)
        }
    }
}