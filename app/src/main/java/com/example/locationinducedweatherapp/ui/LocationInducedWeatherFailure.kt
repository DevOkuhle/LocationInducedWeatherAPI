package com.example.locationinducedweatherapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel

@Composable
fun LocationInducedWeatherFailure(locationInducedViewModel: LocationInducedViewModel) = with(locationInducedViewModel) {
    Column(
        modifier = modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = modifier.weight(3f)
                .fillMaxWidth()
        ) {
            Icon(
                modifier = modifier.padding(dimensionResource(R.dimen.dimension_30dp)),
                painter = painterResource(R.drawable.error_icon),
                contentDescription = stringResource(id = R.string.error_icon_description)
            )
            Text(
                modifier = modifier.fillMaxWidth()
                    .padding(
                        top = dimensionResource(R.dimen.dimension_20dp),
                        start = dimensionResource(R.dimen.dimension_8dp),
                        end = dimensionResource(R.dimen.dimension_8dp)
                    ),
                text = locationInducedViewModel.failureResponse.failureMessage,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Box(
            modifier = modifier.fillMaxSize(1f)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { locationInducedViewModel.navigateToLocationInducedWeatherReportScreen() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.dimension_65dp))
            ) {
                Text(
                    text = stringResource(R.string.home_screen),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}