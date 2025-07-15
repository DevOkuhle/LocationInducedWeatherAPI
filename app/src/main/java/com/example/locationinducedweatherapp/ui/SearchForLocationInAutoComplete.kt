package com.example.locationinducedweatherapp.ui

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel

@Composable
fun SearchForLocationInAutoComplete(locationInducedViewModel: LocationInducedViewModel) = with(locationInducedViewModel) {
    val context = LocalContext.current
    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
    val googlePlacesResult = locationInducedViewModel.searchedGooglePlace.collectAsState().value

    Column(modifier = modifier.fillMaxSize()) {
        if (googlePlacesResult.isNotEmpty()) {
            googleServicesResponseHandle(googlePlacesResult)
        }

        AndroidView(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.dimension_16dp)),
            factory = { ctx ->
                FrameLayout(ctx).apply {
                    id = View.generateViewId()
                    fragmentManager.beginTransaction()
                        .replace(this.id, SearchForLocationUsingAutoCompleteFragment())
                        .commit()
                }
            }
        )
    }
}
