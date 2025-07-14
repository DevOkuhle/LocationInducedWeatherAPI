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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.model.ComposableFunctionAttributes
import com.example.locationinducedweatherapp.util.DisplayCircularProgressIndicator
import com.example.locationinducedweatherapp.util.SetUpScaffoldTopBar
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel

@Composable
fun ViewFavouriteLocationProfiles(composableFunctionAttributes: ComposableFunctionAttributes, locationInducedViewModel: LocationInducedViewModel) = with(composableFunctionAttributes) {
    var loading by rememberSaveable { mutableStateOf(true) }
    locationInducedViewModel.readUserFavouriteLocationProfiles()
    Scaffold(modifier = modifier.fillMaxSize(),
        topBar = {
            SetUpScaffoldTopBar(composableFunctionAttributes)
        }
    ) { innerPadding ->
        if (loading) {
            DisplayCircularProgressIndicator(modifier = modifier.fillMaxWidth(), innerPadding)
            locationInducedViewModel.userFavouriteLocationProfiles = locationInducedViewModel.readUserFavouriteLocationProfiles.collectAsState().value
            if (locationInducedViewModel.userFavouriteLocationProfiles.isNotEmpty()) {
                loading = false
            }
        } else {
            Column(modifier = modifier.padding(innerPadding)) {
                LazyColumn {
                    itemsIndexed(locationInducedViewModel.userFavouriteLocationProfiles) { index, userFavouriteLocationProfile ->
                        Column(
                            modifier = modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .clickable {
                                    locationInducedViewModel.selectedFavouriteLocationProfileIndex = index
                                    locationInducedViewModel.profileSelectedClickAction(composableFunctionAttributes)
                                    locationInducedViewModel.showWeatherForecastForFavouriteLocation(true)
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