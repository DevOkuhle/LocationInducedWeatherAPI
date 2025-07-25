package com.example.locationinducedweatherapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel

@Composable
fun DisplayCircularProgressIndicator(modifier: Modifier, paddingValues: PaddingValues? = null) {
    if (paddingValues != null) {
        modifier.padding(paddingValues)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Loading...", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = modifier.height(dimensionResource(R.dimen.dimension_16dp)))
            CircularProgressIndicator(
                modifier = Modifier.size(dimensionResource(R.dimen.dimension_80dp)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetUpScaffoldTopBar(locationInducedViewModel: LocationInducedViewModel) = with(locationInducedViewModel){
    TopAppBar(
        modifier = modifier.height(dimensionResource(R.dimen.dimension_65dp))
            .fillMaxWidth(),
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
                    .padding(top = dimensionResource(R.dimen.dimension_4dp)),
                text = stringResource(R.string.favourite_location),
                style = MaterialTheme.typography.titleSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = { navigationController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

