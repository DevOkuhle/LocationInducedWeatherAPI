package com.example.locationinducedweatherapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.locationinducedweatherapp.data.model.ComposableFunctionAttributes
import com.example.locationinducedweatherapp.ui.navigation.LocationInducedWeatherNavigationGraph
import com.example.locationinducedweatherapp.ui.theme.LocationInducedWeatherAppTheme
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var locationInducedViewModel: LocationInducedViewModel
    private lateinit var navigationController: NavHostController
    private val modifier: Modifier = Modifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationInducedWeatherAppTheme {
                locationInducedViewModel = hiltViewModel<LocationInducedViewModel>()
                navigationController = rememberNavController()
                val composableFunctionAttributes = ComposableFunctionAttributes(
                    modifier = modifier,
                    navigationController = navigationController
                )
                LocationInducedWeatherNavigationGraph(composableFunctionAttributes, locationInducedViewModel)
            }
        }
    }
}