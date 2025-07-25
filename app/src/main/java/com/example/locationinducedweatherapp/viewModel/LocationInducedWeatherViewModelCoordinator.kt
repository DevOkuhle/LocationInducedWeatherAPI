package com.example.locationinducedweatherapp.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.model.CurrentWeatherMappedAttributes
import com.example.locationinducedweatherapp.data.model.RowItemValues
import com.example.locationinducedweatherapp.data.model.request.LocationWeatherAttributesRequest
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.PeriodWeatherConditions
import com.example.locationinducedweatherapp.data.model.response.forecast.Weather
import com.example.locationinducedweatherapp.data.model.response.forecast.WeatherConditionsNumericalSummary
import com.example.locationinducedweatherapp.room.entitties.SavedLocationWeatherForecast
import com.example.locationinducedweatherapp.room.entitties.UserFavouriteLocationProfiles
import com.example.locationinducedweatherapp.util.Constants
import com.example.locationinducedweatherapp.util.Constants.Companion.DAY_OF_THE_WEEK_FORMATTER
import com.example.locationinducedweatherapp.util.Constants.Companion.DEGREE_CHARACTER
import com.example.locationinducedweatherapp.util.Constants.Companion.FAILURE_STATE_WEATHER_FORECAST
import com.example.locationinducedweatherapp.util.Constants.Companion.LAST_UPDATED_DATE_AND_TIME
import com.example.locationinducedweatherapp.util.Constants.Companion.ONE_SECOND
import com.example.locationinducedweatherapp.util.Constants.Companion.SECOND
import com.example.locationinducedweatherapp.util.Constants.Companion.SUCCESS_STATE_WEATHER_FORECAST
import com.example.locationinducedweatherapp.util.Constants.Companion.UNIT_ONCE
import com.example.locationinducedweatherapp.util.FailureTypeEnum
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LocationInducedWeatherViewModelCoordinator(private val locationInducedViewModel: LocationInducedViewModel, private val locationInducedWeatherRoomViewModel: LocationInducedWeatherRoomViewModel) {

    fun recordLocationGenerallyOrAsFavourite(locationGridPoints: String, isUserAddingAFavouriteLocation: Boolean = true) = with(locationInducedViewModel) {
        val savedLocationWeatherForecast = SavedLocationWeatherForecast(
            favouriteLocationName = locationInducedWeatherRoomViewModel.userGivenNameFavouriteLocation ?: "",
            currentWeatherType = locationInducedViewModel.locationInducedCurrentWeatherResponse.weatherDescription.first().main,
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
        locationInducedWeatherRoomViewModel.addUserFavouriteLocations(savedLocationWeatherForecast)

        if (isUserAddingAFavouriteLocation) {
            val userFavouriteLocationProfiles = UserFavouriteLocationProfiles(
                cityName = locationInducedCurrentWeatherResponse.name,
                country = locationInducedCurrentWeatherResponse.locationCityDetails.country,
                coordinates = locationGridPoints,
                userGiveName = locationInducedWeatherRoomViewModel.userGivenNameFavouriteLocation ?: ""
            )
            locationInducedWeatherRoomViewModel.addUserFavouriteLocationProfiles(userFavouriteLocationProfiles)
            locationInducedWeatherRoomViewModel.userGivenNameFavouriteLocation = null
        }
    }

    private fun handleAppFailureResponses() = with(locationInducedViewModel) {
        val isInternetFailure = failureResponse.failureType == FailureTypeEnum.Internet && !isNetworkAvailable()
        when {
            isInternetFailure && selectedFavouriteLocationProfileIndex != -1 -> locationInducedWeatherRoomViewModel.userLocationsInducedWeatherByCoordinates(userFavouriteLocationProfiles[selectedFavouriteLocationProfileIndex].coordinates)
            isInternetFailure -> locationInducedWeatherRoomViewModel.readUserPreviousLocationsInducedWeather()
            else -> navigateToLocationInducedWeatherFailureScreen()
        }
    }

    fun performResponseHandling(weatherAPISuccessfulFlag: Int, updateLoadingStatus: () -> Unit) = with(locationInducedViewModel) {
        if (weatherAPISuccessfulFlag == SUCCESS_STATE_WEATHER_FORECAST) {
            locationInducedWeatherRoomViewModel.showWeatherForecastForFavouriteLocation(false)
            updateLoadingStatus()
            setWeatherAPISuccessfulFlag(-1)
            successStateIncrementer = 0
        } else if (weatherAPISuccessfulFlag == FAILURE_STATE_WEATHER_FORECAST) {
            handleAppFailureResponses()
        }
    }

    fun profileSelectedClickAction() = with(locationInducedViewModel) {
        var selectedFavouriteLocationProfile = userFavouriteLocationProfiles[selectedFavouriteLocationProfileIndex].coordinates.split(";")
        val locationWeatherAttributesRequest = LocationWeatherAttributesRequest(
            latitude = selectedFavouriteLocationProfile.first().trim().toDouble(),
            longitude = selectedFavouriteLocationProfile.last().trim().toDouble(),
            apiKey = Constants.OPEN_WEATHER_API_KEY
        )
        currentUserCoordinates = "${locationWeatherAttributesRequest.latitude},${locationWeatherAttributesRequest.longitude}"
        getAllLocationBasedWeatherInformation(locationWeatherAttributesRequest)
        navigateToLocationInducedWeatherReportScreen()

    }

    fun setUpLocationInducedWeatherResponse(savedLocationWeatherForecast: List<SavedLocationWeatherForecast>) = with(locationInducedViewModel) {
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

    fun formatTimestamp(timestamp: Long): String = SimpleDateFormat(LAST_UPDATED_DATE_AND_TIME, Locale.getDefault()).format(Date(timestamp * ONE_SECOND))

    fun <T> changeListIntoAString(inputList: List<T>): String {
        var addValuesToString = ""
        inputList.forEachIndexed { index, inputItem -> addValuesToString += if (index == inputList.size - 1) "$inputItem" else "$inputItem;" }
        return addValuesToString
    }

    fun convertTimestampIntoDayOfTheWeek(timeStamp: Int): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timeStamp.toLong() * ONE_SECOND }
        val calenderDayOfTheWeek = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK)) }
        val sdf = SimpleDateFormat(DAY_OF_THE_WEEK_FORMATTER, Locale.getDefault())
        return sdf.format(calenderDayOfTheWeek.time)
    }

    fun isNetworkAvailable(): Boolean = with(locationInducedViewModel) {
        val connectivityManager = application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getRowItemAveragesForDayForecast(daysOfTheWeekIndex: Int, periodWeatherCondition: PeriodWeatherConditions, dayOfTheWeek: String, periodWeatherConditionsIndex: Int): RowItemValues = with(locationInducedViewModel){
        var averageTemperature = ""
        var imageResourceIdentifier = mapWeatherValuesAndResource(periodWeatherCondition.weather.first().main)
        forecastIconResourceIdentifier.add(imageResourceIdentifier)

        if (periodWeatherConditionsIndex == 0) {
            averageTemperature = "${periodWeatherCondition.weatherConditionsNumericalSummary.temperature.toInt()}$DEGREE_CHARACTER"
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

    fun mapWeatherValuesAndResource(weatherType: String): Int = with(locationInducedViewModel) {
        val weatherStatuses = application.applicationContext.resources.getStringArray(R.array.weatherStatuses).toList()
        return when {
            weatherStatuses.first().contains(weatherType, ignoreCase = true) -> R.drawable.rain_large
            weatherStatuses.last().contains(weatherType, ignoreCase = true) -> R.drawable.sunny_large
            else -> R.drawable.partly_sunny_large
        }
    }

    fun initializeLocationGoogleServices() = with(locationInducedViewModel) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application.applicationContext)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, SECOND).setMaxUpdates(UNIT_ONCE).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val getLastLocation = locationResult.lastLocation
                populateGridPointsAndInvokeOpenWeatherAPI(
                    getLastLocation?.latitude,
                    getLastLocation?.longitude,
                    application.applicationContext.resources.getString(R.string.coordinates_error)
                )
                currentUserCoordinates = "${getLastLocation?.latitude};${getLastLocation?.longitude}"
                fusedLocationClient.removeLocationUpdates(this)
                setLocationRequested(true)
            }
        }
        locationSettingsRequestBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true)
        settingsClient = LocationServices.getSettingsClient(application)
    }

    fun addValuesToMutableList(dayOfTheWeek: String, periodWeatherCondition: PeriodWeatherConditions) = with(locationInducedViewModel) {
        if (!forecastDays.contains(dayOfTheWeek)) {
            forecastDays.add(dayOfTheWeek)
        }
        sameDayTemperatureValues.add(periodWeatherCondition.weatherConditionsNumericalSummary.temperature)
    }

    fun mapCurrentWeatherToResources(weatherType: String): CurrentWeatherMappedAttributes = with(locationInducedViewModel){
        val weatherStatuses = application.applicationContext.resources.getStringArray(R.array.weatherStatuses).toList()
        return when {
            weatherStatuses.first().contains(weatherType, ignoreCase = true) -> CurrentWeatherMappedAttributes(
                weatherType = weatherStatuses.first(),
                imageResource = R.drawable.forest_rainy,
                backgroundColorResource = R.color.color_for_rainy
            )
            weatherStatuses.last().contains(weatherType, ignoreCase = true) -> CurrentWeatherMappedAttributes(
                weatherType = weatherStatuses.last(),
                imageResource = R.drawable.forest_sunny,
                backgroundColorResource = R.color.color_for_sunny
            )
            else -> CurrentWeatherMappedAttributes(
                weatherType = weatherStatuses[1],
                imageResource = R.drawable.forest_cloudy,
                backgroundColorResource = R.color.color_for_cloudy
            )
        }
    }
}