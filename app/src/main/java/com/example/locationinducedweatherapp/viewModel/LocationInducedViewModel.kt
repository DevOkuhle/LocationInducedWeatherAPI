package com.example.locationinducedweatherapp.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.api.APIResponseHandler
import com.example.locationinducedweatherapp.data.model.FailureResponse
import com.example.locationinducedweatherapp.data.model.RowItemValues
import com.example.locationinducedweatherapp.data.model.request.LocationWeatherAttributesRequest
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.GridPoints
import com.example.locationinducedweatherapp.data.model.response.forecast.LocationInducedForecastWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.PeriodWeatherConditions
import com.example.locationinducedweatherapp.data.model.response.forecast.Weather
import com.example.locationinducedweatherapp.data.model.response.forecast.WeatherConditionsNumericalSummary
import com.example.locationinducedweatherapp.repository.weather.LocationInducedWeatherRepository
import com.example.locationinducedweatherapp.room.entitties.SavedLocationWeatherForecast
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import com.example.locationinducedweatherapp.ui.navigation.LocationInducedWeatherNavigationScreen
import com.example.locationinducedweatherapp.util.Constants
import com.example.locationinducedweatherapp.util.Constants.Companion.DEGREE_CHARACTER
import com.example.locationinducedweatherapp.util.Constants.Companion.FAILURE_STATE
import com.example.locationinducedweatherapp.util.Constants.Companion.SECOND
import com.example.locationinducedweatherapp.util.Constants.Companion.SUCCESS_STATE
import com.example.locationinducedweatherapp.util.Constants.Companion.UNIT_ONCE
import com.example.locationinducedweatherapp.util.FailureTypeEnum
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
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
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class LocationInducedViewModel @Inject constructor(private val locationInducedWeatherRepository: LocationInducedWeatherRepository, private val application: Application): ViewModel() {

    private val _currentLocationWeatherInformationMutableStateFlow =
        MutableStateFlow(LocationInducedCurrentWeatherResponse())
    val currentLocationWeatherInformationMutableStateFlow =
        _currentLocationWeatherInformationMutableStateFlow.asStateFlow()

    private val _locationWeatherForecastMutableStateFlow =
        MutableStateFlow(LocationInducedForecastWeatherResponse())
    val locationWeatherForecastMutableStateFlow =
        _locationWeatherForecastMutableStateFlow.asStateFlow()

    private val _userLocationsInducedWeather =
        MutableStateFlow(emptyList<SavedLocationWeatherForecast>())
    val userLocationsInducedWeather = _userLocationsInducedWeather.asStateFlow()

    private val _failureResponseMutableStateFlow = MutableStateFlow(FailureResponse())
    val failureResponseMutableStateFlow = _failureResponseMutableStateFlow.asStateFlow()

    private var _readUserFavouriteLocationProfiles = MutableStateFlow(emptyList<UserFavouriteLocationProfiles>())
    val readUserFavouriteLocationProfiles = _readUserFavouriteLocationProfiles.asStateFlow()

    private var _doesLocationAlreadyExist = MutableStateFlow(-1)
    var doesLocationAlreadyExist = _doesLocationAlreadyExist.asStateFlow()

    private var _weatherAPISuccessfulFlag = MutableStateFlow(-1)
    var weatherAPISuccessfulFlag = _weatherAPISuccessfulFlag.asStateFlow()

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

    private var _searchedGooglePlace = MutableStateFlow("")
    val searchedGooglePlace = _searchedGooglePlace.asStateFlow()

    private var _showLocationPermissionDescriptionDialog = MutableStateFlow(false)
    val showLocationPermissionDescriptionDialog = _showLocationPermissionDescriptionDialog.asStateFlow()

    lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    lateinit var navigationController: NavHostController
    lateinit var modifier: Modifier
    lateinit var checkLocationSettings: Task<LocationSettingsResponse>
    var locationInducedCurrentWeatherResponse: LocationInducedCurrentWeatherResponse = LocationInducedCurrentWeatherResponse()
    var locationInducedForecastWeatherResponse: LocationInducedForecastWeatherResponse = LocationInducedForecastWeatherResponse()
    var failureResponse: FailureResponse = FailureResponse()
    var isPermanentlyDeclined: Boolean? = null
    var wasWeatherForecastSuccessful: Boolean? = false
    lateinit var locationCoordinates: GridPoints
    var sameDayTemperatureValues: MutableList<Double> = mutableListOf()
    var averageForecastTemperatures: MutableList<String> = mutableListOf()
    var forecastDays: MutableList<String> = mutableListOf()
    var forecastIconResourceIdentifier: MutableList<Int> = mutableListOf()
    var userGivenNameFavouriteLocation: String? = null
    var userFavouriteLocationProfiles: List<UserFavouriteLocationProfiles> = emptyList()
    var selectedFavouriteLocationProfileIndex: Int = -1
    var currentUserCoordinates: String = ""
    var daysForecastRowItems: MutableList<RowItemValues> = mutableListOf()
    var searchPlaceInGoogle: LatLng? = null
    var isInvocationFromGooglePlaces: Boolean = false
    var isOffLineMode: Boolean = false
    var lastUpdatedDate: String = ""
    var successStateIncrementer: Int = 0

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, SECOND).setMaxUpdates(UNIT_ONCE)
            .build()
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val getLastLocation = locationResult.lastLocation
            populateGridPointsAndInvokeOpenWeatherAPI(
                getLastLocation?.latitude,
                getLastLocation?.longitude,
                application.applicationContext.resources.getString(R.string.coordinates_error)
            )
            currentUserCoordinates = "${getLastLocation?.latitude};${getLastLocation?.longitude}"
            fusedLocationClient.removeLocationUpdates(this)
            _locationRequested.update { true }
        }
    }

    companion object {
        const val FAILURE_STATE_WEATHER_FORECAST = 0
        const val SUCCESS_STATE_WEATHER_FORECAST = 2
        const val ONE_SECOND = 1000
    }

    fun navigateToLocationInducedWeatherReportScreen() =
        navigationController.navigate(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherReportScreen.route)

    fun navigateToViewFavouriteLocationProfilesScreen() =
        navigationController.navigate(route = LocationInducedWeatherNavigationScreen.ViewFavouriteLocationProfilesScreen.route)

    fun navigateToViewAllFavouriteLocationsInGoogleMapsScreen() =
        navigationController.navigate(route = LocationInducedWeatherNavigationScreen.ViewAllFavouriteLocationsInGoogleMapsScreen.route)

    fun viewUserGooglePlacesScreen() =
        navigationController.navigate(route = LocationInducedWeatherNavigationScreen.ViewUserGooglePlacesScreen.route)

    fun navigateToLocationInducedWeatherFailureScreen() {
        navigationController.navigate(route = LocationInducedWeatherNavigationScreen.LocationInducedWeatherFailureScreen.route)
        setWeatherAPISuccessfulFlag(-1)
    }

    fun evaluateGPSLocationSuccess() {
        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(application)
        checkLocationSettings = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())

        checkLocationSettings.addOnSuccessListener {
            _gpsUserEnabled.update { true }
        }
    }

    fun addValuesToMutableList(dayOfTheWeek: String, periodWeatherCondition: PeriodWeatherConditions) {
        if (!forecastDays.contains(dayOfTheWeek)) {
            forecastDays.add(dayOfTheWeek)
        }
        sameDayTemperatureValues.add(periodWeatherCondition.weatherConditionsNumericalSummary.temperature)
    }

    fun getRowItemAveragesForDayForecast(daysOfTheWeekIndex: Int, periodWeatherCondition: PeriodWeatherConditions, dayOfTheWeek: String, periodWeatherConditionsIndex: Int): RowItemValues {
        var averageTemperature = ""
        var imageResourceIdentifier = mapWeatherValuesAndResource(periodWeatherCondition.weather.first().main)
        forecastIconResourceIdentifier.add(imageResourceIdentifier)

        if (periodWeatherConditionsIndex == 0) {
            averageTemperature = "${periodWeatherCondition.weatherConditionsNumericalSummary.temperature}$DEGREE_CHARACTER"
            averageForecastTemperatures.add(averageTemperature)
            forecastDays.add(dayOfTheWeek)
        } else {
            averageTemperature = "${sameDayTemperatureValues.average().toInt()}$DEGREE_CHARACTER"
            averageForecastTemperatures.add(averageTemperature)
        }

        return RowItemValues(
            currentTemperature = averageTemperature,
            imageResourceIdentifier = imageResourceIdentifier,
            dayOfTheWeek = forecastDays[daysOfTheWeekIndex],
            textStyle = TextStyle.Default,
        )
    }

    fun mapWeatherValuesAndResource(weatherType: String): Int {
        val weatherStatuses = application.applicationContext.resources.getStringArray(R.array.weatherStatuses).toList()
        return when {
            weatherStatuses.first().contains(weatherType, ignoreCase = true) -> R.drawable.rain_large
            weatherStatuses.last().contains(weatherType, ignoreCase = true) -> R.drawable.sunny_large
            else -> R.drawable.partly_sunny_large
        }
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

    fun getAllLocationBasedWeatherInformation(locationWeatherAttributesRequest: LocationWeatherAttributesRequest) {
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

    private fun <T> invokeAPICallsAndPopulateStateFlows(locationWeatherMutableStateFlow: MutableStateFlow<T>, isFiveDayForecast: Boolean = false, apiWeatherInvocation: suspend() -> Flow<APIResponseHandler<T>>) {
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
                        successStateIncrementer ++
                        _weatherAPISuccessfulFlag.update { successStateIncrementer}
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

    fun addUserFavouriteLocations(savedLocationWeatherForecast: SavedLocationWeatherForecast) {
        viewModelScope.launch {
            locationInducedWeatherRepository.addUserPreviousLocationsInducedWeather(savedLocationWeatherForecast)
        }
    }

    fun readUserLocationsInducedWeather() {
        viewModelScope.launch {
            locationInducedWeatherRepository.readUserPreviousLocationsInducedWeather().collectLatest { userPreviousLocationsInducedWeather ->
                _userLocationsInducedWeather.update { userPreviousLocationsInducedWeather }
            }
        }
    }

    fun userLocationsInducedWeatherByCoordinates(locationGridPoint: String) {
        viewModelScope.launch {
            locationInducedWeatherRepository.getUserLocationsInducedWeatherByCoordinates(locationGridPoint).collectLatest { userLocationsInducedWeatherByCoordinates ->
                _userLocationsInducedWeather.update { userLocationsInducedWeatherByCoordinates }
            }
        }
    }

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
                _doesLocationAlreadyExist.update { if (doesLocationAlreadyExist) FAILURE_STATE else SUCCESS_STATE }
            }
        }

    fun setUserLocationsInducedWeather(userLocationsInducedWeather: List<SavedLocationWeatherForecast>) {
        _userLocationsInducedWeather.update { userLocationsInducedWeather }
    }

    fun shouldDismissAlertDialog(shouldDismissAlertDialog: Boolean) {
        _shouldDismissAlertDialog.update { shouldDismissAlertDialog }
    }

    fun setShouldAddEntityEntry(shouldAddEntityEntry: Boolean) {
        _shouldAddEntityEntry.update { shouldAddEntityEntry }
    }

    fun setWeatherAPISuccessfulFlag(updateWeatherAPISuccessfulFlag: Int) {
        _weatherAPISuccessfulFlag.update { updateWeatherAPISuccessfulFlag }
    }

    fun shouldShowMenuItems(shouldShowMenuItems: Boolean) {
        _shouldShowMenuItems.update { shouldShowMenuItems }
    }

    fun setGPSUserEnabled(gpsUserEnabled: Boolean) {
        _gpsUserEnabled.update { gpsUserEnabled }
    }

    fun setShowLocationPermissionDescriptionDialog(showLocationPermissionDescriptionDialog: Boolean) {
        _showLocationPermissionDescriptionDialog.update { showLocationPermissionDescriptionDialog }
    }

    fun showWeatherForecastForFavouriteLocation(showWeatherForecastForFavouriteLocation: Boolean) {
        _showWeatherForecastForFavouriteLocation.update { showWeatherForecastForFavouriteLocation }
    }

    fun performResponseHandling(weatherAPISuccessfulFlag: Int, updateLoadingStatus: () -> Unit) {
        if (weatherAPISuccessfulFlag == SUCCESS_STATE_WEATHER_FORECAST) {
            showWeatherForecastForFavouriteLocation(false)
            updateLoadingStatus()
            setWeatherAPISuccessfulFlag(-1)
            successStateIncrementer = 0
        } else if (weatherAPISuccessfulFlag == FAILURE_STATE_WEATHER_FORECAST) {
            handleAppFailureResponses()
        }
    }

    fun passInValuesFromGooglePlaces(newValue: String) {
        _searchedGooglePlace.update { newValue }
    }

    fun doesLocationAlreadyExist(setFlag: Int) {
        _doesLocationAlreadyExist.update { setFlag }
    }

    fun setFailureResponseMutableStateFlow(failureResponse: FailureResponse) {
        _failureResponseMutableStateFlow.update { failureResponse }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun handleAppFailureResponses() {
        val isInternetFailure = failureResponse.failureType == FailureTypeEnum.Internet && !isNetworkAvailable()
        when {
            isInternetFailure && selectedFavouriteLocationProfileIndex != -1 -> userLocationsInducedWeatherByCoordinates(userFavouriteLocationProfiles[selectedFavouriteLocationProfileIndex].coordinates)
            isInternetFailure -> readUserLocationsInducedWeather()
            else -> navigateToLocationInducedWeatherFailureScreen()
        }
    }

    fun setUpLocationInducedWeatherResponse(savedLocationWeatherForecast: List<SavedLocationWeatherForecast>) {
        isOffLineMode = true
        locationInducedCurrentWeatherResponse = LocationInducedCurrentWeatherResponse(
            weatherConditionsNumericalSummary = WeatherConditionsNumericalSummary(
                temperature = savedLocationWeatherForecast.last().currentTemperature,
                maximumTemperature = savedLocationWeatherForecast.last().currentMaximumTemperature,
                minimumTemperature = savedLocationWeatherForecast.last().currentMinimumTemperature
            ),
            weatherDescription = listOf(Weather(main = savedLocationWeatherForecast.last().currentWeatherType)),
        )
        lastUpdatedDate = savedLocationWeatherForecast.last().weatherForecastTimeStamp
        val averageTemperatures = savedLocationWeatherForecast.last().averageTemperatures.split(";")
        val daysOfForecasts = savedLocationWeatherForecast.last().forecastDays.split(";")
        val imageResourceIdentifiers = savedLocationWeatherForecast.last().iconResourceIdentifiers.split(";")
        repeat(5) { index ->
            daysForecastRowItems.add(RowItemValues(
                currentTemperature = averageTemperatures[index],
                imageResourceIdentifier = imageResourceIdentifiers[index].toInt(),
                dayOfTheWeek = daysOfForecasts[index],
                textStyle = TextStyle.Default,
            ))
        }
    }

    fun convertTimestampIntoDayOfTheWeek(timeStamp: Int): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeStamp.toLong() * ONE_SECOND
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
        inputList.forEachIndexed { index, inputItem ->
            addValuesToString += if (index == inputList.size - 1) {
                "$inputItem"
            } else {
                "$inputItem;"
            }
        }
        return addValuesToString
    }

    fun profileSelectedClickAction(locationInducedViewModel: LocationInducedViewModel) {
        var selectedFavouriteLocationProfile = userFavouriteLocationProfiles[selectedFavouriteLocationProfileIndex].coordinates.split(";")
        val locationWeatherAttributesRequest = LocationWeatherAttributesRequest(
            latitude = selectedFavouriteLocationProfile.first().trim().toDouble(),
            longitude = selectedFavouriteLocationProfile.last().trim().toDouble(),
            apiKey = Constants.OPEN_WEATHER_API_KEY
        )
        currentUserCoordinates = "${locationWeatherAttributesRequest.latitude},${locationWeatherAttributesRequest.longitude}"
        getAllLocationBasedWeatherInformation(locationWeatherAttributesRequest)
        locationInducedViewModel.navigateToLocationInducedWeatherReportScreen()

    }

    fun formatTimestamp(timestamp: Long): String {
        val convertTimestampToMillis = timestamp * ONE_SECOND
        val sdf = SimpleDateFormat("dd-MM-yyyy-HH:mm", Locale.getDefault())
        val date = Date(convertTimestampToMillis)
        return sdf.format(date)
    }

    fun recordLocationGenerallyOrAsFavourite(locationGridPoints: String, isUserAddingAFavouriteLocation: Boolean = true) {
        val savedLocationWeatherForecast = SavedLocationWeatherForecast(
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
            country = locationInducedForecastWeatherResponse.locationCityDetails.country,
            weatherForecastTimeStamp = formatTimestamp(locationInducedCurrentWeatherResponse.dateExecuted)
        )
        addUserFavouriteLocations(savedLocationWeatherForecast)

        if (isUserAddingAFavouriteLocation) {
            val userFavouriteLocationProfiles = UserFavouriteLocationProfiles(
                cityName = locationInducedCurrentWeatherResponse.name,
                country = locationInducedCurrentWeatherResponse.locationCityDetails.country,
                coordinates = locationGridPoints,
                userGiveName = userGivenNameFavouriteLocation ?: ""
            )
            addUserFavouriteLocationProfiles(userFavouriteLocationProfiles)
            userGivenNameFavouriteLocation = null
        }
    }
}