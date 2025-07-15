package com.example.locationinducedweatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class SearchForLocationUsingAutoCompleteFragment : Fragment() {
    private val locationInducedViewModel: LocationInducedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.show_search_by_auto_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.view_places_on_google)
                as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) =
                locationInducedViewModel.passInValuesFromGooglePlaces("${place.location?.latitude};${place.location?.longitude}")

            override fun onError(errorStatus: Status) =
                locationInducedViewModel.passInValuesFromGooglePlaces(errorStatus.statusMessage.toString())
        })
    }
}