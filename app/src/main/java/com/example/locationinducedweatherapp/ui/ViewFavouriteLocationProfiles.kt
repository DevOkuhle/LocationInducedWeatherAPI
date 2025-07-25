package com.example.locationinducedweatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.model.PackageLocationInducedWeatherViewModels

@Composable
fun ViewFavouriteLocationProfiles(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    var loading by rememberSaveable { mutableStateOf(true) }
    var userCountryInput by rememberSaveable { mutableStateOf("") }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { SetUpScaffoldTopBar(packageLocationInducedWeatherViewModels.locationInducedViewModel) }
    ) { innerPadding ->
        if (loading) {
            packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.readUserFavouriteLocationProfiles()
            DisplayCircularProgressIndicator(modifier = modifier.fillMaxWidth(), innerPadding)
            userFavouriteLocationProfiles = packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.readUserFavouriteLocationProfiles.collectAsState().value
            if (userFavouriteLocationProfiles.isNotEmpty()) {
                loading = false
            }
        } else {
            Column(modifier = modifier.padding(innerPadding)) {
                if (userFavouriteLocationProfiles.size > 10) {
                    Column(
                        modifier = modifier.fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        OutlinedTextField(
                            value = userCountryInput,
                            onValueChange = { userCountryInput = it },
                            label = { Text(text = stringResource(R.string.search_for_your_favourite)) },
                            modifier = modifier.fillMaxWidth()
                                .padding(dimensionResource(R.dimen.dimension_8dp))
                        )
                        Box(
                            modifier = modifier.padding(dimensionResource(R.dimen.dimension_8dp))
                                .fillMaxWidth()
                                .height(dimensionResource(R.dimen.dimension_2dp))
                                .background(if (isSystemInDarkTheme()) Color.LightGray else Color.Black)
                        )
                    }
                }

                LazyColumn {
                    itemsIndexed(userFavouriteLocationProfiles) { index, userFavouriteLocationProfile ->
                        Column(
                            modifier = modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .clickable {
                                    selectedFavouriteLocationProfileIndex = index
                                    packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator.profileSelectedClickAction()
                                    packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.showWeatherForecastForFavouriteLocation(true)
                                }
                        ) {
                            Text(
                                modifier = modifier.padding(
                                    start = dimensionResource(R.dimen.dimension_8dp),
                                    top = dimensionResource(R.dimen.dimension_8dp)
                                ),
                                text = userFavouriteLocationProfile.userGiveName,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                modifier = modifier.padding(dimensionResource(R.dimen.dimension_8dp)),
                                text = "${userFavouriteLocationProfile.cityName}, ${userFavouriteLocationProfile.country}",
                                style = MaterialTheme.typography.displaySmall
                            )
                            Box(
                                modifier = modifier.fillMaxWidth()
                                    .height(dimensionResource(R.dimen.dimension_2dp))
                                    .background(if (isSystemInDarkTheme()) Color.LightGray else Color.Black)
                                    .padding(
                                        top = dimensionResource(R.dimen.dimension_16dp),
                                        bottom = dimensionResource(R.dimen.dimension_16dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}