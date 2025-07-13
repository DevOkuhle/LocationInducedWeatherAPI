package com.example.locationinducedweatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.locationinducedweatherapp.data.model.ComposableFunctionAttributes
import com.example.locationinducedweatherapp.ui.LocationInducedWeatherFailure
import com.example.locationinducedweatherapp.ui.LocationInducedWeatherReport
import com.example.locationinducedweatherapp.ui.ViewFavouriteLocationProfiles
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel

@Composable
fun LocationInducedWeatherNavigationGraph (composableFunctionAttributes: ComposableFunctionAttributes, locationInducedViewModel: LocationInducedViewModel) = with(composableFunctionAttributes) {
    NavHost(navController = navigationController, startDestination = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route) {
        composable(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route) {
            LocationInducedWeatherReport(composableFunctionAttributes, locationInducedViewModel)
        }

        composable(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherFailureScreen.route) {
            LocationInducedWeatherFailure(composableFunctionAttributes, locationInducedViewModel)
        }

        composable(route = LocationInducedWeatherNavigationScreen.ViewFavouriteLocationProfilesScreen.route) {
            ViewFavouriteLocationProfiles(composableFunctionAttributes, locationInducedViewModel)
        }
    }
}