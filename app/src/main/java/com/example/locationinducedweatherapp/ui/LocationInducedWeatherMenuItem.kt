package com.example.locationinducedweatherapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.model.PackageLocationInducedWeatherViewModels
import com.example.locationinducedweatherapp.util.Constants.Companion.FAILURE_STATE
import com.example.locationinducedweatherapp.util.Constants.Companion.SUCCESS_STATE
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationInducedWeatherMenuItem(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    val shouldShowMenuItems = shouldShowMenuItems.collectAsState().value
    IconButton(
        modifier = modifier.padding(dimensionResource(R.dimen.dimension_30dp))
            .size(dimensionResource(R.dimen.dimension_30dp)),
        onClick = { shouldShowMenuItems(!shouldShowMenuItems) }
    ) {
        Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.more_options))
    }
    DropdownMenu(
        modifier = modifier.width(LocalConfiguration.current.screenWidthDp.dp/2),
        expanded = shouldShowMenuItems,
        onDismissRequest = { shouldShowMenuItems(false) },
        offset = DpOffset(x = 0.dp, y = (-80).dp)
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.add_favourite_location)) },
            onClick = {
                packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.setShouldAddEntityEntry(true)
                shouldShowMenuItems(false)
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.view_favourites)) },
            onClick = {
                shouldShowMenuItems(false)
                navigateToViewFavouriteLocationProfilesScreen()            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.view_favourites_in_maps)) },
            onClick = {
                shouldShowMenuItems(false)
                navigateToViewAllFavouriteLocationsInGoogleMapsScreen()            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.autocomplete_search)) },
            onClick = {
                shouldShowMenuItems(false)
                navigateToViewUserGooglePlacesScreen()            }
        )
    }
}

@Composable
fun AddAProfileForSavedFavouriteLocation(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    val locationGridPoints = "${locationCoordinates.latitude};${locationCoordinates.longitude}"
    packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.doesLocationAlreadyExist()
    val doesLocationAlreadyExist = packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.doesLocationAlreadyExist.collectAsState().value

    when (doesLocationAlreadyExist) {
        SUCCESS_STATE -> {
            AddFavouriteLocationIfItDoesNotAlreadyExists(packageLocationInducedWeatherViewModels, true, locationGridPoints)
            packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.doesLocationAlreadyExist(-1)
        }
        FAILURE_STATE -> {
            AddFavouriteLocationIfItDoesNotAlreadyExists(packageLocationInducedWeatherViewModels, false, locationGridPoints)
            packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.doesLocationAlreadyExist(-1)
        }
    }
}

@Composable
fun AddFavouriteLocationIfItDoesNotAlreadyExists(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels, isUserInputNeeded: Boolean, locationGridPoints: String) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    var userFavouriteLocationName by remember { mutableStateOf("") }
    var title = if (isUserInputNeeded) stringResource(R.string.user_input_message) else ""
    AlertDialog(
        onDismissRequest = {
            shouldDismissAlertDialog(true)
            packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.setShouldAddEntityEntry(false)
        },
        title = { Text(text = title) },
        text = {
            if (isUserInputNeeded) {
                TextField(
                    value = userFavouriteLocationName,
                    onValueChange = { newValue ->
                        userFavouriteLocationName = newValue
                    },
                    label = { Text(stringResource(R.string.user_input_message)) },
                    singleLine = true
                )
            } else {
                Text(
                    text = stringResource(R.string.location_already_exists),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        },
        confirmButton = {
            Button(
                onClick = {
                    if (isUserInputNeeded) {
                        packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.userGivenNameFavouriteLocation = userFavouriteLocationName
                        packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator.recordLocationGenerallyOrAsFavourite(locationGridPoints)
                    }
                    shouldDismissAlertDialog(true)
                    packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.setShouldAddEntityEntry(false)
                },
                enabled = if (isUserInputNeeded) userFavouriteLocationName.trim().length > 3 else true
            ) {
                Text(stringResource(R.string.ok_button), style = MaterialTheme.typography.titleSmall)
            }
        }
    )
}