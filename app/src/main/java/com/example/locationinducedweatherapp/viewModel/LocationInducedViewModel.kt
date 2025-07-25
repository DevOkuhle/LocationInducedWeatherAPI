package com.example.locationinducedweatherapp.viewModel

import android.app.Application
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.locationinducedweatherapp.data.api.APIResponseHandler
import com.example.locationinducedweatherapp.data.model.FailureResponse
import com.example.locationinducedweatherapp.data.model.RowItemValues
import com.example.locationinducedweatherapp.data.model.request.LocationWeatherAttributesRequest
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.*
import com.example.locationinducedweatherapp.repository.weather.LocationInducedWeatherRepository
import com.example.locationinducedweatherapp.room.entitties.*
import com.example.locationinducedweatherapp.ui.navigation.LocationInducedWeatherNavigationScreen
import com.example.locationinducedweatherapp.util.Constants
import com.example.locationinducedweatherapp.util.Constants.Companion.FAILURE_STATE_WEATHER_FORECAST
import com.example.locationinducedweatherapp.util.Constants.Companion.SUCCESS_STATE_WEATHER_FORECAST
import com.example.locationinducedweatherapp.util.FailureTypeEnum
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class LocationInducedViewModel @Inject constructor(private val locationInducedWeatherRepository: LocationInducedWeatherRepository, val application: Application): ViewModel() {
    private val _currentLocationWeatherInformationMutableStateFlow = MutableStateFlow(LocationInducedCurrentWeatherResponse())
    val currentLocationWeatherInformationMutableStateFlow = _currentLocationWeatherInformationMutableStateFlow.asStateFlow()

    private val _locationWeatherForecastMutableStateFlow = MutableStateFlow(LocationInducedForecastWeatherResponse())
    val locationWeatherForecastMutableStateFlow = _locationWeatherForecastMutableStateFlow.asStateFlow()

    private val _failureResponseMutableStateFlow = MutableStateFlow(FailureResponse())
    val failureResponseMutableStateFlow = _failureResponseMutableStateFlow.asStateFlow()

    private var _weatherAPISuccessfulFlag = MutableStateFlow(-1)
    var weatherAPISuccessfulFlag = _weatherAPISuccessfulFlag.asStateFlow()

    private var _locationRequested = MutableStateFlow(false)
    val locationRequested = _locationRequested.asStateFlow()

    private var _gpsUserEnabled = MutableStateFlow(false)
    val gpsUserEnabled = _gpsUserEnabled

    private var _shouldDismissAlertDialog = MutableStateFlow(false)
    val shouldDismissAlertDialog = _shouldDismissAlertDialog.asStateFlow()

    private var _shouldShowMenuItems = MutableStateFlow(false)
    var shouldShowMenuItems = _shouldShowMenuItems.asStateFlow()

    private var _searchedGooglePlace = MutableStateFlow("")
    val searchedGooglePlace = _searchedGooglePlace.asStateFlow()

    private var _showLocationPermissionDescriptionDialog = MutableStateFlow(false)
    val showLocationPermissionDescriptionDialog = _showLocationPermissionDescriptionDialog.asStateFlow()

    lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    lateinit var navigationController: NavHostController
    lateinit var modifier: Modifier
    lateinit var checkLocationSettings: Task<LocationSettingsResponse>
    lateinit var locationCoordinates: GridPoints
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var locationSettingsRequestBuilder: LocationSettingsRequest. Builder
    lateinit var settingsClient: SettingsClient
    var locationInducedCurrentWeatherResponse: LocationInducedCurrentWeatherResponse = LocationInducedCurrentWeatherResponse()
    var locationInducedForecastWeatherResponse: LocationInducedForecastWeatherResponse = LocationInducedForecastWeatherResponse()
    var failureResponse: FailureResponse = FailureResponse()
    var isPermanentlyDeclined: Boolean? = null
    var wasWeatherForecastSuccessful: Boolean? = false
    var sameDayTemperatureValues: MutableList<Double> = mutableListOf()
    var averageForecastTemperatures: MutableList<String> = mutableListOf()
    var forecastDays: MutableList<String> = mutableListOf()
    var forecastIconResourceIdentifier: MutableList<Int> = mutableListOf()
    var userFavouriteLocationProfiles: List<UserFavouriteLocationProfiles> = emptyList()
    var selectedFavouriteLocationProfileIndex: Int = -1
    var currentUserCoordinates: String = ""
    var daysForecastRowItems: MutableList<RowItemValues> = mutableListOf()
    var searchPlaceInGoogle: LatLng? = null
    var isInvocationFromGooglePlaces: Boolean = false
    var isOffLineMode: Boolean = false
    var lastUpdatedDate: String = ""
    var successStateIncrementer: Int = 0

    fun navigateToLocationInducedWeatherReportScreen() = navigationController.navigate(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route)

    fun navigateToViewFavouriteLocationProfilesScreen() = navigationController.navigate(route = LocationInducedWeatherNavigationScreen.ViewFavouriteLocationProfilesScreen.route)

    fun navigateToViewAllFavouriteLocationsInGoogleMapsScreen() = navigationController.navigate(route = LocationInducedWeatherNavigationScreen.ViewAllFavouriteLocationsInGoogleMapsScreen.route)

    fun navigateToViewUserGooglePlacesScreen() = navigationController.navigate(route = LocationInducedWeatherNavigationScreen.ViewUserGooglePlacesScreen.route)

    fun navigateToLocationInducedWeatherFailureScreen() {
        navigationController.navigate(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherFailureScreen.route)
        setWeatherAPISuccessfulFlag(-1)
    }

    fun evaluateGPSLocationSuccess() {
        checkLocationSettings = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())
        checkLocationSettings.addOnSuccessListener { _gpsUserEnabled.update { true } }
    }

    fun googleServicesResponseHandle(googlePlacesResult: String) {
        if (googlePlacesResult.contains(";")) {
            isInvocationFromGooglePlaces = true
            searchPlaceInGoogle = LatLng(googlePlacesResult.split(";").first().toDouble(), googlePlacesResult.split(";").last().toDouble())
            navigateToViewAllFavouriteLocationsInGoogleMapsScreen()
        } else {
            failureResponse = FailureResponse(failureType = FailureTypeEnum.GeneralErrorFailures, failureMessage = googlePlacesResult)
            navigateToLocationInducedWeatherFailureScreen()        }
    }

    fun populateGridPointsAndInvokeOpenWeatherAPI(latitude: Double?, longitude: Double?, coordinateDefaultError: String) {
        if (latitude == null || longitude == null) {
            _failureResponseMutableStateFlow.update { FailureResponse(failureMessage = coordinateDefaultError) }
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

    fun getAllLocationBasedWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherForecastResponse = async { getLocationWeatherForecast(locationWeatherAttributesRequest) }
                val currentWeatherResponse = async { getCurrentWeatherInformation(locationWeatherAttributesRequest) }
                weatherForecastResponse.await()
                currentWeatherResponse.await()
            } catch (e: Exception) {
                _failureResponseMutableStateFlow.update { FailureResponse(failureMessage = e.message ?: "") }
            }
        }

    fun getCurrentWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) = invokeAPICallsAndPopulateStateFlows(_currentLocationWeatherInformationMutableStateFlow) {
        locationInducedWeatherRepository.getCurrentWeatherInformation(locationWeatherAttributesRequest)
    }

    fun getLocationWeatherForecast(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) = invokeAPICallsAndPopulateStateFlows(_locationWeatherForecastMutableStateFlow) {
        locationInducedWeatherRepository.getWeatherForecastInformation(locationWeatherAttributesRequest)
    }

    private fun <T> invokeAPICallsAndPopulateStateFlows(locationWeatherMutableStateFlow: MutableStateFlow<T>, apiWeatherInvocation: suspend() -> Flow<APIResponseHandler<T>>) {
        viewModelScope.launch {
            apiWeatherInvocation().collectLatest { response ->
                when (response) {
                    is APIResponseHandler.Failure -> {
                        _weatherAPISuccessfulFlag.update { FAILURE_STATE_WEATHER_FORECAST }
                        wasWeatherForecastSuccessful = false
                        response.failureResponse?.let { failureResponse ->
                            _failureResponseMutableStateFlow.update { failureResponse }
                        }
                    }

                    is APIResponseHandler.Success -> {
                        successStateIncrementer++
                        _weatherAPISuccessfulFlag.update { successStateIncrementer }
                        if (successStateIncrementer == SUCCESS_STATE_WEATHER_FORECAST) {
                            wasWeatherForecastSuccessful = true
                        }
                        response.successResponse?.let { weatherResponse ->
                            locationWeatherMutableStateFlow.update { weatherResponse }
                        }
                    }
                }
            }
        }
    }

    fun shouldDismissAlertDialog(shouldDismissAlertDialog: Boolean) = _shouldDismissAlertDialog.update { shouldDismissAlertDialog }

    fun setWeatherAPISuccessfulFlag(updateWeatherAPISuccessfulFlag: Int) = _weatherAPISuccessfulFlag.update { updateWeatherAPISuccessfulFlag }

    fun shouldShowMenuItems(shouldShowMenuItems: Boolean) = _shouldShowMenuItems.update { shouldShowMenuItems }

    fun setGPSUserEnabled(gpsUserEnabled: Boolean) = _gpsUserEnabled.update { gpsUserEnabled }

    fun setLocationRequested(isLocationRequested: Boolean) = _locationRequested.update { isLocationRequested }

    fun setShowLocationPermissionDescriptionDialog(showLocationPermissionDescriptionDialog: Boolean) = _showLocationPermissionDescriptionDialog.update { showLocationPermissionDescriptionDialog }

    fun passInValuesFromGooglePlaces(newValue: String) = _searchedGooglePlace.update { newValue }

    fun setFailureResponseMutableStateFlow(failureResponse: FailureResponse) = _failureResponseMutableStateFlow.update { failureResponse }
}