package com.example.locationinducedweatherapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.locationinducedweatherapp.ui.navigation.LocationInducedWeatherNavigationGraph
import com.example.locationinducedweatherapp.ui.theme.LocationInducedWeatherAppTheme
import com.example.locationinducedweatherapp.util.Constants.Companion.GPS_STATUS_CODE
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var locationInducedViewModel: LocationInducedViewModel
    private val modifier: Modifier = Modifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationInducedWeatherAppTheme {
                locationInducedViewModel = hiltViewModel<LocationInducedViewModel>()
                locationInducedViewModel.navigationController = rememberNavController()
                locationInducedViewModel.modifier = modifier
                LocationInducedWeatherNavigationGraph(locationInducedViewModel)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GPS_STATUS_CODE) {
            if (resultCode == RESULT_OK) {
                locationInducedViewModel.setGPSUserEnabled(true)
                locationInducedViewModel.setShowLocationPermissionDescriptionDialog(false)
            } else {
                locationInducedViewModel.setShowLocationPermissionDescriptionDialog(true)
            }
        }
    }
}