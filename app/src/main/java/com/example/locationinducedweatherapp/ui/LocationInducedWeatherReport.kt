package com.example.locationinducedweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.model.ComposableFunctionAttributes
import com.example.locationinducedweatherapp.data.model.RowItemValues
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.locationinducedweatherapp.data.model.CurrentWeatherMappedAttributes
import com.example.locationinducedweatherapp.data.model.RememberSaveAblePassObject
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.data.model.response.forecast.PeriodWeatherConditions
import com.example.locationinducedweatherapp.ui.navigation.LocationInducedWeatherNavigationScreen
import com.example.locationinducedweatherapp.util.Constants.Companion.ANDROID_PACKAGE
import com.example.locationinducedweatherapp.util.Constants.Companion.DEGREE_CHARACTER
import com.example.locationinducedweatherapp.util.DisplayCircularProgressIndicator
import kotlin.String

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun LocationInducedWeatherReport(composableFunctionAttributes: ComposableFunctionAttributes, locationInducedViewModel: LocationInducedViewModel) = with(composableFunctionAttributes) {
    var loading by rememberSaveable { mutableStateOf(true) }
    var locationPermissionGranted by rememberSaveable { mutableStateOf(false) }
    var showLocationPermissionDescriptionDialog by rememberSaveable { mutableStateOf(false) }
    val localActivity = LocalActivity.current
    val context = LocalContext.current
    val rememberSaveAblePassObjectForLoading = RememberSaveAblePassObject(
        saveAbleStateFlow = loading,
        setSaveAbleStateFlowToFalse = { loading = false},
        setSaveAbleStateFlowToTrue = { loading = true }
    )
    val rememberSaveAblePassObjectForDialog = RememberSaveAblePassObject(
        saveAbleStateFlow = showLocationPermissionDescriptionDialog,
        setSaveAbleStateFlowToFalse = { showLocationPermissionDescriptionDialog = false},
        setSaveAbleStateFlowToTrue = { showLocationPermissionDescriptionDialog = true }
    )
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val shouldShowWeatherForecastForFavouriteLocation = locationInducedViewModel.showWeatherForecastForFavouriteLocation.collectAsState().value
    val shouldDismissAlertDialog = locationInducedViewModel.shouldDismissAlertDialog.collectAsState().value
    val locationRequested = locationInducedViewModel.locationRequested.collectAsState().value
    when {
        shouldDismissAlertDialog -> {
            locationInducedViewModel.shouldDismissAlertDialog(false)
            loading = false
        }

        loading && !locationPermissionGranted && !shouldShowWeatherForecastForFavouriteLocation -> {
            DisplayCircularProgressIndicator(modifier = modifier.fillMaxWidth())
            RequestLocationPermissionAndPopulateStateFlow(locationInducedViewModel, rememberSaveAblePassObjectForLoading, rememberSaveAblePassObjectForDialog) { locationPermissionGranted = true }
        }

        loading && !locationRequested && !shouldShowWeatherForecastForFavouriteLocation -> {
            DisplayCircularProgressIndicator(modifier = modifier.fillMaxWidth())
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                locationInducedViewModel.evaluateGPSLocationSuccess()
                if (locationInducedViewModel.gpsUserEnabled.collectAsState().value){
                    LaunchedEffect(Unit) {
                        locationInducedViewModel.fusedLocationClient.requestLocationUpdates(locationInducedViewModel.locationRequest, locationInducedViewModel.locationCallback, Looper.getMainLooper())
                    }
                }
            }
        }

        loading &&  (shouldShowWeatherForecastForFavouriteLocation || locationInducedViewModel.locationRequested.collectAsState().value) -> {
            DisplayCircularProgressIndicator(modifier = Modifier.fillMaxWidth())
            locationInducedViewModel.locationInducedCurrentWeatherResponse = locationInducedViewModel.currentLocationWeatherInformationMutableStateFlow.collectAsState().value
            locationInducedViewModel.locationInducedForecastWeatherResponse = locationInducedViewModel.locationWeatherForecastMutableStateFlow.collectAsState().value
            locationInducedViewModel.failureMessage = locationInducedViewModel.failureResponseMutableStateFlow.collectAsState().value
            if (locationInducedViewModel.isWeatherAPISuccessful == true && locationInducedViewModel.checkIfMutableStateIsNotCached()) {
                locationInducedViewModel.showWeatherForecastForFavouriteLocation(false)
                loading = false
            } else if (locationInducedViewModel.failureMessage.isNotEmpty()) {
                navigationController.navigate(LocationInducedWeatherNavigationScreen.LocationInducedWeatherFailureScreen.route)
            }
        }

        showLocationPermissionDescriptionDialog -> {
            LocationPermissionDescriptionDialog(
                isPermanentlyDeclined = locationInducedViewModel.isPermanentlyDeclined,
                onDismissed = { showLocationPermissionDescriptionDialog = false },
                onAcceptButton = {
                    showLocationPermissionDescriptionDialog = false
                    locationInducedViewModel.permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                onGoToAppSetting = {
                    localActivity?.let { activity ->
                        val settingsIntent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts(ANDROID_PACKAGE, localActivity.packageName, null)
                        )
                        activity.startActivity(settingsIntent)
                    }
                }
            )
        }

        else -> {
            Column(
                modifier = modifier.fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val locationInducedCurrentWeatherResponse = locationInducedViewModel.locationInducedCurrentWeatherResponse
                val locationInducedForecastWeatherResponse = locationInducedViewModel.locationInducedForecastWeatherResponse
                val currentWeatherMappedAttributes = mapCurrentWeatherToResources(locationInducedCurrentWeatherResponse.weatherDescription.first().main)
                Box(
                    modifier = modifier.weight(2f)
                        .fillMaxSize()
                        .background(colorResource(currentWeatherMappedAttributes.backgroundColorResource))
                ) {
                    Image(
                        modifier = modifier.fillMaxSize(),
                        painter = painterResource(currentWeatherMappedAttributes.imageResource),
                        contentDescription = stringResource(R.string.image_description),
                        contentScale = ContentScale.Fit
                    )

                    if (locationInducedViewModel.shouldAddEntityEntry.collectAsState().value) {
                        AddAProfileForSavedFavouriteLocation(locationInducedViewModel)
                    } else {
                        Column(modifier = modifier.align(Alignment.TopStart)) {
                            LocationInducedWeatherMenuItem(composableFunctionAttributes, locationInducedViewModel)
                        }
                    }
                    UpperScreenUISetup(modifier, currentWeatherMappedAttributes, locationInducedCurrentWeatherResponse)
                }
                Column(
                    modifier = modifier.weight(3f)
                        .fillMaxSize()
                        .background(colorResource(currentWeatherMappedAttributes.backgroundColorResource))
                ) {
                    LowerScreenSetUp(modifier, locationInducedCurrentWeatherResponse)
                    FormatingOfAveragesAndDisplaying(modifier, locationInducedViewModel, locationInducedForecastWeatherResponse.periodWeatherConditions)
                }
            }
            locationInducedViewModel.previousUserCoordinates = locationInducedViewModel.currentUserCoordinates
            locationInducedViewModel.previousLocationInducedCurrentWeatherResponse = locationInducedViewModel.locationInducedCurrentWeatherResponse
        }
    }
}

@Composable
fun FormatingOfAveragesAndDisplaying(modifier: Modifier, locationInducedViewModel: LocationInducedViewModel, periodWeatherConditions: List<PeriodWeatherConditions>) {
    periodWeatherConditions.forEach { periodWeatherConditions ->
        val dayOfTheWeek = locationInducedViewModel.convertTimestampIntoDayOfTheWeek(periodWeatherConditions.timeOfDataForecast)
        when {
            locationInducedViewModel.previousDayOfTheWeek.isEmpty() -> {
                locationInducedViewModel.previousDayOfTheWeek = dayOfTheWeek
                locationInducedViewModel.sameDayTemperatureValues.add(periodWeatherConditions.weatherConditionsNumericalSummary.temperature)
            }
            locationInducedViewModel.previousDayOfTheWeek == dayOfTheWeek -> {
                locationInducedViewModel.sameDayTemperatureValues.add(periodWeatherConditions.weatherConditionsNumericalSummary.temperature)
            }
            locationInducedViewModel.previousDayOfTheWeek != dayOfTheWeek-> {
                var averageTemperature = "${locationInducedViewModel.sameDayTemperatureValues.average().toInt()}$DEGREE_CHARACTER"
                var imageResourceIdentifier = getMapForecastWeatherTypeToWeatherImageIcon(periodWeatherConditions.weather.first().main)
                val rowItemValues = RowItemValues(
                    currentTemperature = averageTemperature,
                    imageResourceIdentifier = imageResourceIdentifier,
                    dayOfTheWeek = locationInducedViewModel.previousDayOfTheWeek,
                    textStyle = TextStyle.Default,
                )
                locationInducedViewModel.averageForecastTemperatures.add(averageTemperature)
                locationInducedViewModel.forecastIconResourceIdentifier.add(imageResourceIdentifier)
                locationInducedViewModel.forecastDays.add(locationInducedViewModel.previousDayOfTheWeek)
                WeatherForecastRowItem(modifier, rowItemValues)
                locationInducedViewModel.sameDayTemperatureValues.clear()
                locationInducedViewModel.previousDayOfTheWeek = dayOfTheWeek
                locationInducedViewModel.sameDayTemperatureValues.add(periodWeatherConditions.weatherConditionsNumericalSummary.temperature)
            }
        }
    }
}

@Composable
fun LowerScreenSetUp(modifier: Modifier, locationInducedCurrentWeatherResponse: LocationInducedCurrentWeatherResponse) {
    val weatherNumericalSummary = locationInducedCurrentWeatherResponse.weatherConditionsNumericalSummary
    val rowItemValuesOne = RowItemValues(
        minTemperature = "${weatherNumericalSummary.minimumTemperature.toInt()}$DEGREE_CHARACTER",
        currentTemperature = "${weatherNumericalSummary.temperature.toInt()}$DEGREE_CHARACTER",
        maxTemperature = "${weatherNumericalSummary.maximumTemperature.toInt()}$DEGREE_CHARACTER",
        textStyle = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )
    val rowItemValuesTwo = RowItemValues(
        minTemperature = stringResource(R.string.min_temperature),
        currentTemperature = stringResource(R.string.current_temperature),
        maxTemperature = stringResource(R.string.max_temperature),
        textStyle = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Normal
    )
    CurrentWeatherRowItem(modifier, rowItemValuesOne)
    CurrentWeatherRowItem(modifier, rowItemValuesTwo)
    Box(
        modifier = modifier.fillMaxWidth()
            .height(dimensionResource(R.dimen.dimension_2dp))
            .background(Color.LightGray)
            .padding(
                top = dimensionResource(R.dimen.dimension_4dp),
                bottom = dimensionResource(R.dimen.dimension_8dp)
            )
    )
}

@Composable
fun UpperScreenUISetup(modifier: Modifier, currentWeatherMappedAttributes: CurrentWeatherMappedAttributes, locationInducedCurrentWeatherResponse: LocationInducedCurrentWeatherResponse) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${locationInducedCurrentWeatherResponse.weatherConditionsNumericalSummary.temperature.toInt()}$DEGREE_CHARACTER",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = currentWeatherMappedAttributes.weatherType.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun getMapForecastWeatherTypeToWeatherImageIcon(weatherType: String): Int {
    val weatherStatuses = stringArrayResource(R.array.weatherStatuses).toList()
    return when {
        weatherStatuses.first().contains(weatherType, ignoreCase = true) -> R.drawable.rain_large
        weatherStatuses.last().contains(weatherType, ignoreCase = true) -> R.drawable.sunny_large
        else -> R.drawable.partly_sunny_large
    }
}

@Composable
private fun mapCurrentWeatherToResources(weatherType: String): CurrentWeatherMappedAttributes {
    val weatherStatuses = stringArrayResource(R.array.weatherStatuses).toList()
    return when {
        weatherStatuses.first().contains(weatherType, ignoreCase = true) -> CurrentWeatherMappedAttributes(
            weatherType = weatherStatuses.first(),
            imageResource = R.drawable.forest_rainy,
            backgroundColorResource = R.color.color_for_rainy
        )
        weatherStatuses.last().contains(weatherType, ignoreCase = true) -> CurrentWeatherMappedAttributes(
            weatherType = weatherStatuses.first(),
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

@Composable
private fun WeatherForecastRowItem(modifier: Modifier, rowItemValues: RowItemValues) = with(rowItemValues) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier.weight(1f)
                .padding(
                    start = dimensionResource(R.dimen.dimension_8dp),
                    top = dimensionResource(R.dimen.dimension_16dp)
                ),
            text = dayOfTheWeek,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = modifier.weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = modifier.padding(top = dimensionResource(R.dimen.dimension_16dp)),
                painter = painterResource(id = imageResourceIdentifier),
                contentDescription = stringResource(R.string.image_description),
                contentScale = ContentScale.Fit
            )
        }
        Text(
            modifier = modifier.weight(1f)
                .padding(
                    end = dimensionResource(R.dimen.dimension_8dp),
                    top = dimensionResource(R.dimen.dimension_16dp)
                ),
            text = currentTemperature,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

    }
}

@Composable
private fun CurrentWeatherRowItem(modifier: Modifier, rowItemValues: RowItemValues) = with(rowItemValues){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier.weight(1f)
                .padding(start = dimensionResource(R.dimen.dimension_8dp)),
            text = minTemperature,
            textAlign = TextAlign.Start,
            style = textStyle,
            fontWeight = fontWeight
        )
        Text(
            modifier = Modifier.weight(1f),
            text = currentTemperature,
            textAlign = TextAlign.Center,
            style = textStyle,
            fontWeight = fontWeight
        )
        Text(
            modifier = Modifier.weight(1f)
                .padding(end = dimensionResource(R.dimen.dimension_8dp)),
            text = maxTemperature,
            textAlign = TextAlign.End,
            style = textStyle,
            fontWeight = fontWeight
        )
    }
}

@Composable
fun RequestLocationPermissionAndPopulateStateFlow(locationInducedViewModel: LocationInducedViewModel, rememberSaveAblePassObjectForLoading: RememberSaveAblePassObject, rememberSaveAblePassObjectForDialog: RememberSaveAblePassObject, onLocationPermissionGrantedMutableState: () -> Unit) {
    val localActivity = LocalActivity.current
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    locationInducedViewModel.permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted ->
        if (isGranted) {
            if (!rememberSaveAblePassObjectForLoading.saveAbleStateFlow) {
                rememberSaveAblePassObjectForLoading.setSaveAbleStateFlowToTrue
            }
            onLocationPermissionGrantedMutableState()
        } else {
            localActivity?.let { localActivity ->
                 locationInducedViewModel.isPermanentlyDeclined = !shouldShowRequestPermissionRationale(localActivity, permission)
            }
            rememberSaveAblePassObjectForDialog.setSaveAbleStateFlowToTrue
            rememberSaveAblePassObjectForLoading.setSaveAbleStateFlowToFalse
        }
    })

    LaunchedEffect(Unit) {
        locationInducedViewModel.permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}