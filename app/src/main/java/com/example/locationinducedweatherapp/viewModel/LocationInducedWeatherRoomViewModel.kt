package com.example.locationinducedweatherapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationinducedweatherapp.repository.room.LocationInducedWeatherRoomRepository
import com.example.locationinducedweatherapp.room.entitties.SavedLocationWeatherForecast
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import com.example.locationinducedweatherapp.util.Constants.Companion.FAILURE_STATE
import com.example.locationinducedweatherapp.util.Constants.Companion.SUCCESS_STATE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationInducedWeatherRoomViewModel @Inject constructor(private val locationInducedWeatherRoomRepository: LocationInducedWeatherRoomRepository): ViewModel() {
    private var _readUserFavouriteLocationProfiles = MutableStateFlow(emptyList<UserFavouriteLocationProfiles>())
    val readUserFavouriteLocationProfiles = _readUserFavouriteLocationProfiles.asStateFlow()

    private var _doesLocationAlreadyExist = MutableStateFlow(-1)
    var doesLocationAlreadyExist = _doesLocationAlreadyExist.asStateFlow()

    private val _previousUserLocationsInducedWeather = MutableStateFlow(emptyList<SavedLocationWeatherForecast>())
    val previousUserLocationsInducedWeather = _previousUserLocationsInducedWeather.asStateFlow()

    private var _showWeatherForecastForFavouriteLocation = MutableStateFlow(false)
    var showWeatherForecastForFavouriteLocation = _showWeatherForecastForFavouriteLocation.asStateFlow()

    private var _shouldAddEntityEntry = MutableStateFlow(false)
    var shouldAddEntityEntry = _shouldAddEntityEntry.asStateFlow()

    var userGivenNameFavouriteLocation: String? = null

    fun addUserFavouriteLocations(savedLocationWeatherForecast: SavedLocationWeatherForecast) = viewModelScope.launch {
        locationInducedWeatherRoomRepository.addUserPreviousLocationsInducedWeather(savedLocationWeatherForecast)
    }

    fun readUserPreviousLocationsInducedWeather() = viewModelScope.launch {
        locationInducedWeatherRoomRepository.readUserPreviousLocationsInducedWeather().collectLatest { userPreviousLocationsInducedWeather ->
            _previousUserLocationsInducedWeather.update { userPreviousLocationsInducedWeather }
        }
    }

    fun addUserFavouriteLocationProfiles(getUserFavouriteLocationProfiles: UserFavouriteLocationProfiles) = viewModelScope.launch {
        locationInducedWeatherRoomRepository.addUserFavouriteLocationProfiles(getUserFavouriteLocationProfiles)
    }

    fun readUserFavouriteLocationProfiles() = viewModelScope.launch {
        locationInducedWeatherRoomRepository.readUserFavouriteLocationProfiles().collectLatest { favouriteProfile ->
            _readUserFavouriteLocationProfiles.update { favouriteProfile }
        }
    }

    fun doesLocationAlreadyExist() = viewModelScope.launch {
        locationInducedWeatherRoomRepository.readUserFavouriteLocationProfiles().collectLatest { favouriteProfile ->
            _doesLocationAlreadyExist.update { if (favouriteProfile.isNotEmpty()) FAILURE_STATE else SUCCESS_STATE }
        }
    }

    fun userLocationsInducedWeatherByCoordinates(locationGridPoint: String) = viewModelScope.launch {
        locationInducedWeatherRoomRepository.getUserLocationsInducedWeatherByCoordinates(locationGridPoint).collectLatest { userLocationsInducedWeatherByCoordinates ->
            _previousUserLocationsInducedWeather.update { userLocationsInducedWeatherByCoordinates }
        }
    }

    fun showWeatherForecastForFavouriteLocation(showWeatherForecastForFavouriteLocation: Boolean) = _showWeatherForecastForFavouriteLocation.update { showWeatherForecastForFavouriteLocation }

    fun doesLocationAlreadyExist(setFlag: Int) = _doesLocationAlreadyExist.update { setFlag }

    fun setUserLocationsInducedWeather(userLocationsInducedWeather: List<SavedLocationWeatherForecast>) = _previousUserLocationsInducedWeather.update { userLocationsInducedWeather }

    fun setShouldAddEntityEntry(shouldAddEntityEntry: Boolean) = _shouldAddEntityEntry.update { shouldAddEntityEntry }
}