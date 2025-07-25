package com.example.locationinducedweatherapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.model.PackageLocationInducedWeatherViewModels
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ViewAllFavouriteLocationsInGoogleMaps(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    var loading by rememberSaveable { mutableStateOf(true) }
    if (loading) {
        DisplayCircularProgressIndicator(modifier)
        if (!isInvocationFromGooglePlaces) {
            packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.readUserFavouriteLocationProfiles()
        }

        userFavouriteLocationProfiles = packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.readUserFavouriteLocationProfiles.collectAsState().value
        if (userFavouriteLocationProfiles.isNotEmpty() || isInvocationFromGooglePlaces) {
            loading = false
            isInvocationFromGooglePlaces = false
        }
    }
    else {
        val convertCoordinatesIntoNewFormat = userFavouriteLocationProfiles.map { userFavouriteLocationProfile ->
            LatLng(userFavouriteLocationProfile.coordinates.split(";").first().trim().toDouble(), userFavouriteLocationProfile.coordinates.split(";").last().trim().toDouble())
        }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(convertCoordinatesIntoNewFormat.firstOrNull() ?: LatLng(0.0, 0.0), 10f)
        }

        Surface(
            modifier = modifier.fillMaxSize()
        ) {
            Box(modifier = modifier.fillMaxSize()) {
                GoogleMap(cameraPositionState = cameraPositionState) {
                    if (isInvocationFromGooglePlaces) {
                        Marker(
                            state = MarkerState(position = searchPlaceInGoogle ?: LatLng(0.0, 0.0)),
                            title = stringResource(R.string.saved_location),
                            snippet = "${searchPlaceInGoogle?.latitude}, ${searchPlaceInGoogle?.longitude}"
                        )
                        isInvocationFromGooglePlaces = false
                    } else {
                        convertCoordinatesIntoNewFormat.forEach { convertedCoordinates ->
                            Marker(
                                state = MarkerState(position = convertedCoordinates),
                                title = stringResource(R.string.saved_location),
                                snippet = "${convertedCoordinates.latitude}, ${convertedCoordinates.longitude}"
                            )
                        }
                    }
                }
            }
        }
    }
}