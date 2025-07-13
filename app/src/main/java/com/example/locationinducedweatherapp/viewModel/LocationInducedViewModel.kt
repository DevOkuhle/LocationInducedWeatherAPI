package com.example.locationinducedweatherapp.viewModel

import android.Manifest
import android.app.Application
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.api.APIResponseHandler
import com.example.locationinducedweatherapp.data.model.ComposableFunctionAttributes
import com.example.locationinducedweatherapp.data.model.request.LocationWeatherAttributesRequest
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.GridPoints
import com.example.locationinducedweatherapp.data.model.response.forecast.LocationInducedForecastWeatherResponse
import com.example.locationinducedweatherapp.repository.weather.LocationInducedWeatherRepository
import com.example.locationinducedweatherapp.room.entitties.SavedFavourites
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import com.example.locationinducedweatherapp.ui.navigation.LocationInducedWeatherNavigationScreen
import com.example.locationinducedweatherapp.util.Constants
import com.example.locationinducedweatherapp.util.Constants.Companion.SECOND
import com.example.locationinducedweatherapp.util.Constants.Companion.UNIT_ONCE
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationInducedViewModel @Inject constructor(private val locationInducedWeatherRepository: LocationInducedWeatherRepository, private val application: Application): ViewModel() {

    private val _currentLocationWeatherInformationMutableStateFlow = MutableStateFlow(LocationInducedCurrentWeatherResponse())
    val currentLocationWeatherInformationMutableStateFlow = _currentLocationWeatherInformationMutableStateFlow.asStateFlow()

    private val _locationWeatherForecastMutableStateFlow = MutableStateFlow(LocationInducedForecastWeatherResponse())
    val locationWeatherForecastMutableStateFlow = _locationWeatherForecastMutableStateFlow.asStateFlow()

    private val _failureResponseMutableStateFlow = MutableStateFlow("")
    val failureResponseMutableStateFlow = _failureResponseMutableStateFlow.asStateFlow()

    private var _readUserFavouriteLocationProfiles = MutableStateFlow(emptyList<UserFavouriteLocationProfiles>())
    val readUserFavouriteLocationProfiles = _readUserFavouriteLocationProfiles.asStateFlow()

    private var _doesLocationAlreadyExist = MutableStateFlow(false)
    var doesLocationAlreadyExist = _doesLocationAlreadyExist.asStateFlow()

    private var _locationRequested = MutableStateFlow(false)
    val locationRequested = _locationRequested.asStateFlow()

    private var _gpsUserEnabled = MutableStateFlow(false)
    val gpsUserEnabled = _gpsUserEnabled

    private var _shouldDismissAlertDialog = MutableStateFlow(false)
    val shouldDismissAlertDialog = _shouldDismissAlertDialog.asStateFlow()

    private var _shouldAddEntityEntry = MutableStateFlow(false)
    var shouldAddEntityEntry = _shouldAddEntityEntry.asStateFlow()

    private var _shouldShowMenuItems = MutableStateFlow(false)
    var shouldShowMenuItems = _shouldShowMenuItems.asStateFlow()

    private var _showWeatherForecastForFavouriteLocation = MutableStateFlow(false)
    var showWeatherForecastForFavouriteLocation = _showWeatherForecastForFavouriteLocation.asStateFlow()

    lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    var isWeatherAPISuccessful: Boolean? = null
    var locationInducedCurrentWeatherResponse: LocationInducedCurrentWeatherResponse = LocationInducedCurrentWeatherResponse()
    var locationInducedForecastWeatherResponse: LocationInducedForecastWeatherResponse = LocationInducedForecastWeatherResponse()
    var failureMessage: String = ""
    var isPermanentlyDeclined: Boolean = false
    lateinit var locationCoordinates: GridPoints
    var previousDayOfTheWeek: String = ""
    var sameDayTemperatureValues: MutableList<Double> = mutableListOf()
    var averageForecastTemperatures: MutableList<String> = mutableListOf()
    var forecastDays: MutableList<String> = mutableListOf()
    var forecastIconResourceIdentifier: MutableList<Int> = mutableListOf()
    var userGivenNameFavouriteLocation: String? = null
    var userFavouriteLocationProfiles: List<UserFavouriteLocationProfiles> = emptyList()
    var selectedFavouriteLocationProfileIndex: Int = -1
    var currentUserCoordinates: String = ""
    var previousUserCoordinates: String = ""
    var previousLocationInducedCurrentWeatherResponse: LocationInducedCurrentWeatherResponse = LocationInducedCurrentWeatherResponse()


    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application.applicationContext)
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, SECOND).setMaxUpdates(UNIT_ONCE).build()
    val locationCallback =  object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val getLastLocation = locationResult.lastLocation
            populateGridPointsAndInvokeOpenWeatherAPI(getLastLocation?.latitude, getLastLocation?.longitude, application.applicationContext.resources.getString(R.string.coordinates_error))
            currentUserCoordinates = "${getLastLocation?.latitude};${getLastLocation?.longitude}"
            fusedLocationClient.removeLocationUpdates(this)
            _locationRequested.update { true }
        }
    }


    fun evaluateGPSLocationSuccess() {
        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(application)
        val checkLocationSettings = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())

        checkLocationSettings.addOnSuccessListener {
            _gpsUserEnabled.update { true }
        }

        checkLocationSettings.addOnFailureListener { exception ->
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    fun populateGridPointsAndInvokeOpenWeatherAPI(latitude: Double?, longitude: Double?, coordinateDefaultError: String) {
        if (latitude == null || longitude == null) {
            _failureResponseMutableStateFlow.update { coordinateDefaultError }
        } else {

            val locationWeatherAttributesRequest = LocationWeatherAttributesRequest (
                latitude = latitude,
                longitude = longitude,
                apiKey = Constants.OPEN_WEATHER_API_KEY
            )
            locationCoordinates = GridPoints(latitude, longitude)
            getAllLocationBasedWeatherInformation(locationWeatherAttributesRequest)
        }
    }

    fun getAllLocationBasedWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherForecastResponse = async { getLocationWeatherForecast(locationWeatherAttributesRequest) }
                val currentWeatherResponse = async { getCurrentWeatherInformation(locationWeatherAttributesRequest) }
                weatherForecastResponse.await()
                currentWeatherResponse.await()
            } catch (e: Exception) {
                _failureResponseMutableStateFlow.update { e.message ?: "" }
            }
        }
    }

    fun getCurrentWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) {
        invokeAPICallsAndPopulateStateFlows(_currentLocationWeatherInformationMutableStateFlow) {
            locationInducedWeatherRepository.getCurrentWeatherInformation(locationWeatherAttributesRequest)
        }
    }

    fun getLocationWeatherForecast(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) {
        invokeAPICallsAndPopulateStateFlows(_locationWeatherForecastMutableStateFlow) {
            locationInducedWeatherRepository.getWeatherForecastInformation(locationWeatherAttributesRequest)
        }
    }

    private fun <T> invokeAPICallsAndPopulateStateFlows(locationWeatherMutableStateFlow: MutableStateFlow<T>, apiWeatherInvocation: suspend() -> Flow<APIResponseHandler<T>>) {
        viewModelScope.launch {
            apiWeatherInvocation().collectLatest { response ->
                when (response) {
                    is APIResponseHandler.Failure -> {
                        isWeatherAPISuccessful = false
                        response.failureMessage?.let { failureMessage ->
                            _failureResponseMutableStateFlow.update { failureMessage }
                        }
                    }

                    is APIResponseHandler.Success -> {
                        isWeatherAPISuccessful = true
                        response.successResponse?.let { weatherResponse ->
                            locationWeatherMutableStateFlow.update { weatherResponse }
                        }
                    }
                }
            }
        }
    }

    fun addUserFavouriteLocations(savedFavourites: SavedFavourites) {
        viewModelScope.launch {
            locationInducedWeatherRepository.addUserFavouriteLocations(savedFavourites)
        }
    }

    fun readUserFavouriteLocations(): Flow<List<SavedFavourites>> = locationInducedWeatherRepository.readUserFavouriteLocations()

    fun addUserFavouriteLocationProfiles(getUserFavouriteLocationProfiles: UserFavouriteLocationProfiles) {
        viewModelScope.launch {
            locationInducedWeatherRepository.addUserFavouriteLocationProfiles(getUserFavouriteLocationProfiles)
        }
    }

    fun readUserFavouriteLocationProfiles() {
        viewModelScope.launch {
            locationInducedWeatherRepository.readUserFavouriteLocationProfiles().collectLatest { favouriteProfile ->
                    _readUserFavouriteLocationProfiles.update { favouriteProfile }
            }
        }
    }

    fun doesLocationAlreadyExist(locationGridPoint: String) =
        viewModelScope.launch {
            locationInducedWeatherRepository.doesLocationAlreadyExist(locationGridPoint).collectLatest { doesLocationAlreadyExist ->
                _doesLocationAlreadyExist.update { doesLocationAlreadyExist }
            }
        }

    fun shouldDismissAlertDialog(shouldDismissAlertDialog: Boolean) {
        _shouldDismissAlertDialog.update { shouldDismissAlertDialog }
    }

    fun setShouldAddEntityEntry(shouldAddEntityEntry: Boolean) {
        _shouldAddEntityEntry.update { shouldAddEntityEntry }
    }

    fun shouldShowMenuItems(shouldShowMenuItems: Boolean) {
        _shouldShowMenuItems.update { shouldShowMenuItems }
    }
    fun showWeatherForecastForFavouriteLocation(showWeatherForecastForFavouriteLocation: Boolean) {
        _showWeatherForecastForFavouriteLocation.update { showWeatherForecastForFavouriteLocation }
    }

    fun convertTimestampIntoDayOfTheWeek(timeStamp: Int): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeStamp.toLong() * 1000
        }
        val dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val calenderDayOfTheWeek = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfTheWeek)
        }
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(calenderDayOfTheWeek.time)
    }

    fun <T> changeListIntoAString(inputList: List<T>): String {
        var addValuesToString = ""
        inputList.forEach { inputItem ->
            addValuesToString += if (inputList.last() == inputItem) {
                "$inputItem"
            } else {
                "$inputItem;"
            }
        }
        return addValuesToString
    }

    fun profileSelectedClickAction(composableFunctionAttributes: ComposableFunctionAttributes) {
        var selectedFavouriteLocationProfile = userFavouriteLocationProfiles[selectedFavouriteLocationProfileIndex].coordinates.split(";")
        val locationWeatherAttributesRequest = LocationWeatherAttributesRequest(
            latitude = selectedFavouriteLocationProfile.first().trim().toDouble(),
            longitude = selectedFavouriteLocationProfile.last().trim().toDouble(),
            apiKey = Constants.OPEN_WEATHER_API_KEY
        )
        currentUserCoordinates = "${locationWeatherAttributesRequest.latitude},${locationWeatherAttributesRequest.longitude}"
        getAllLocationBasedWeatherInformation(locationWeatherAttributesRequest)
        composableFunctionAttributes.navigationController.navigate(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route)
    }

    fun recordLocationAsFavourite(locationGridPoints: String) {
        val savedFavourites = SavedFavourites(
            favouriteLocationName = userGivenNameFavouriteLocation ?: "",
            currentWeatherType = locationInducedCurrentWeatherResponse.weatherDescription.first().main,
            currentMinimumTemperature = locationInducedCurrentWeatherResponse.weatherConditionsNumericalSummary.minimumTemperature,
            currentMaximumTemperature = locationInducedCurrentWeatherResponse.weatherConditionsNumericalSummary.maximumTemperature,
            currentTemperature = locationInducedCurrentWeatherResponse.weatherConditionsNumericalSummary.temperature,
            averageTemperatures = changeListIntoAString(averageForecastTemperatures.takeLast(5)),
            forecastDays = changeListIntoAString(forecastDays.takeLast(5)),
            iconResourceIdentifiers = changeListIntoAString(forecastIconResourceIdentifier.takeLast(5)),
            locationGridPoint = locationGridPoints,
            cityName = locationInducedForecastWeatherResponse.locationCityDetails.cityName,
            country = locationInducedForecastWeatherResponse.locationCityDetails.country
        )
        val userFavouriteLocationProfiles = UserFavouriteLocationProfiles(
            cityName = locationInducedCurrentWeatherResponse.name,
            country = locationInducedCurrentWeatherResponse.locationCityDetails.country,
            coordinates = locationGridPoints,
            userGiveName = userGivenNameFavouriteLocation ?: ""
        )
        addUserFavouriteLocations(savedFavourites)
        addUserFavouriteLocationProfiles(userFavouriteLocationProfiles)
        userGivenNameFavouriteLocation = null
    }

    fun checkIfMutableStateIsNotCached(): Boolean = locationInducedCurrentWeatherResponse.weatherDescription.isNotEmpty() && locationInducedForecastWeatherResponse.periodWeatherConditions.isNotEmpty()
}