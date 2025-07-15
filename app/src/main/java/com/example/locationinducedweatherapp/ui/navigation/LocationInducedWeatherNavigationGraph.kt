package com.example.locationinducedweatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.locationinducedweatherapp.ui.LocationInducedWeatherFailure
import com.example.locationinducedweatherapp.ui.LocationInducedWeatherReport
import com.example.locationinducedweatherapp.ui.ViewAllFavouriteLocationsInGoogleMaps
import com.example.locationinducedweatherapp.ui.ViewFavouriteLocationProfiles
import com.example.locationinducedweatherapp.ui.SearchForLocationInAutoComplete
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel

@Composable
fun LocationInducedWeatherNavigationGraph(locationInducedViewModel: LocationInducedViewModel) {
    NavHost(navController = locationInducedViewModel.navigationController, startDestination = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route) {
        composable(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route) {
            LocationInducedWeatherReport(locationInducedViewModel)
        }

        composable(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherFailureScreen.route) {
            LocationInducedWeatherFailure(locationInducedViewModel)
        }

        composable(route = LocationInducedWeatherNavigationScreen.ViewFavouriteLocationProfilesScreen.route) {
            ViewFavouriteLocationProfiles(locationInducedViewModel)
        }

        composable(route = LocationInducedWeatherNavigationScreen.ViewAllFavouriteLocationsInGoogleMapsScreen.route) {
            ViewAllFavouriteLocationsInGoogleMaps(locationInducedViewModel)
        }

        composable(route = LocationInducedWeatherNavigationScreen.ViewUserGooglePlacesScreen.route) {
            SearchForLocationInAutoComplete(locationInducedViewModel)
        }
    }
}