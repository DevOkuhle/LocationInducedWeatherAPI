package com.example.locationinducedweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.locationinducedweatherapp.R
import com.example.locationinducedweatherapp.data.model.RowItemValues
import com.example.locationinducedweatherapp.viewModel.LocationInducedViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.locationinducedweatherapp.data.model.CurrentWeatherMappedAttributes
import com.example.locationinducedweatherapp.data.model.FailureResponse
import com.example.locationinducedweatherapp.data.model.RememberSaveAblePassObject
import com.example.locationinducedweatherapp.data.model.response.current.LocationInducedCurrentWeatherResponse
import com.example.locationinducedweatherapp.util.Constants.Companion.DEGREE_CHARACTER
import com.example.locationinducedweatherapp.util.Constants.Companion.GPS_STATUS_CODE
import com.example.locationinducedweatherapp.util.Constants.Companion.PERMISSION_TYPE_GPS_REQUEST
import com.example.locationinducedweatherapp.util.Constants.Companion.PERMISSION_TYPE_LOCATION_REQUEST
import com.example.locationinducedweatherapp.util.Constants.Companion.PERMISSION_TYPE_PERMANENTLY_DECLINED
import com.example.locationinducedweatherapp.util.FailureTypeEnum
import com.google.android.gms.common.api.ResolvableApiException
import kotlin.String
import androidx.core.net.toUri
import com.example.locationinducedweatherapp.data.model.PackageLocationInducedWeatherViewModels

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun LocationInducedWeatherReport(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    var loading by rememberSaveable { mutableStateOf(true) }
    var locationPermissionGranted by rememberSaveable { mutableStateOf(false) }
    val localActivity = LocalActivity.current
    val context = LocalContext.current
    val rememberSaveAblePassObjectForLoading = RememberSaveAblePassObject(
        saveAbleStateFlow = loading,
        setSaveAbleStateFlowToFalse = { loading = false},
        setSaveAbleStateFlowToTrue = { loading = true }
    )
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val shouldShowWeatherForecastForFavouriteLocation = packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.showWeatherForecastForFavouriteLocation.collectAsState().value
    val shouldDismissAlertDialog = shouldDismissAlertDialog.collectAsState().value
    val locationRequested = locationRequested.collectAsState().value
    val userLocationsInducedWeather = packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.previousUserLocationsInducedWeather.collectAsState().value
    val showLocationPermissionDescriptionDialog = showLocationPermissionDescriptionDialog.collectAsState().value

    when {
        userLocationsInducedWeather.isNotEmpty() -> {
            packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator.setUpLocationInducedWeatherResponse(userLocationsInducedWeather)
            packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.setUserLocationsInducedWeather(emptyList())
            loading = false
            setWeatherAPISuccessfulFlag(-1)
        }

        shouldDismissAlertDialog -> {
            shouldDismissAlertDialog(false)
            loading = false
        }

        loading && !locationPermissionGranted && !shouldShowWeatherForecastForFavouriteLocation && !showLocationPermissionDescriptionDialog -> {
            DisplayCircularProgressIndicator(modifier = modifier.fillMaxWidth())
            RequestLocationPermissionAndPopulateStateFlow(packageLocationInducedWeatherViewModels.locationInducedViewModel, rememberSaveAblePassObjectForLoading) { locationPermissionGranted = true }
        }

        loading && locationPermissionGranted && !locationRequested && !shouldShowWeatherForecastForFavouriteLocation && !showLocationPermissionDescriptionDialog-> {
            packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator.initializeLocationGoogleServices()
            DisplayCircularProgressIndicator(modifier = modifier.fillMaxWidth())
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                evaluateGPSLocationSuccess()
                checkLocationSettings.addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            localActivity?.let { activity -> exception.startResolutionForResult(activity, GPS_STATUS_CODE) }
                        } catch (sendEx: IntentSender.SendIntentException) {
                            setFailureResponseMutableStateFlow(FailureResponse(failureType = FailureTypeEnum.GeneralErrorFailures, failureMessage = sendEx.message.toString()))
                        }
                    }
                }

                if (gpsUserEnabled.collectAsState().value){
                    LaunchedEffect(Unit) {
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    }
                }
            }
        }

        loading &&  (shouldShowWeatherForecastForFavouriteLocation || locationRequested) -> {
            DisplayCircularProgressIndicator(modifier = Modifier.fillMaxWidth())
            locationInducedCurrentWeatherResponse = currentLocationWeatherInformationMutableStateFlow.collectAsState().value
            locationInducedForecastWeatherResponse = locationWeatherForecastMutableStateFlow.collectAsState().value
            failureResponse = failureResponseMutableStateFlow.collectAsState().value
            val weatherAPISuccessfulFlag = weatherAPISuccessfulFlag.collectAsState().value
            packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator.performResponseHandling(weatherAPISuccessfulFlag) { loading = false }
            selectedFavouriteLocationProfileIndex = -1
        }

        showLocationPermissionDescriptionDialog -> {
            LocationPermissionDescriptionDialog(
                permissionTypeStatusCode = when (isPermanentlyDeclined) {
                    false -> PERMISSION_TYPE_LOCATION_REQUEST
                    true -> PERMISSION_TYPE_PERMANENTLY_DECLINED
                    else -> PERMISSION_TYPE_GPS_REQUEST
                },
                onDismissed = { setShowLocationPermissionDescriptionDialog(false) },
                onAcceptButton = {
                    setShowLocationPermissionDescriptionDialog(false)
                    loading = true
                },
                onGoToAppSetting = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                    context.startActivity(intent)
                    setShowLocationPermissionDescriptionDialog(false)
                }
            )
        }

        else -> {
            ViewLocationInducedWeatherResults(packageLocationInducedWeatherViewModels)
        }
    }
}

@Composable
fun ViewLocationInducedWeatherResults(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    Column(
        modifier = modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val currentWeatherMappedAttributes = packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator.mapCurrentWeatherToResources(locationInducedCurrentWeatherResponse.weatherDescription.first().main)
        val shouldAddEntityEntry = packageLocationInducedWeatherViewModels.locationInducedWeatherRoomViewModel.shouldAddEntityEntry.collectAsState().value
        Box(
            modifier = modifier.weight(1f)
                .fillMaxSize()
                .background(colorResource(currentWeatherMappedAttributes.backgroundColorResource))
        ) {
            Image(
                painter = painterResource(currentWeatherMappedAttributes.imageResource),
                contentDescription = stringResource(R.string.image_description),
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )

            when {
                shouldAddEntityEntry -> AddAProfileForSavedFavouriteLocation(packageLocationInducedWeatherViewModels)
                !shouldAddEntityEntry -> {
                    Column(modifier = modifier.align(Alignment.TopStart)) {
                        LocationInducedWeatherMenuItem(packageLocationInducedWeatherViewModels)
                    }
                }
            }
            if (isOffLineMode) {
                Column(
                    modifier = modifier.align(Alignment.TopEnd)
                        .padding(
                            end = dimensionResource(R.dimen.dimension_8dp),
                            top = dimensionResource(R.dimen.dimension_30dp)
                        )
                ) {
                    Text(
                        text = stringResource(R.string.offline_mode),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = stringResource(R.string.last_updated_time, lastUpdatedDate),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                lastUpdatedDate = ""
                isOffLineMode = false
            }
            UpperScreenUISetup(modifier, currentWeatherMappedAttributes, locationInducedCurrentWeatherResponse)
        }
        Column(
            modifier = modifier.weight(1f)
                .fillMaxSize()
                .background(colorResource(currentWeatherMappedAttributes.backgroundColorResource))
        ) {
            LowerScreenSetUp(modifier, locationInducedCurrentWeatherResponse)
            PassDataDifferentlyWhenTriggeredFromFavourites(packageLocationInducedWeatherViewModels)
        }
    }
}

@Composable
fun PassDataDifferentlyWhenTriggeredFromFavourites(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    if (daysForecastRowItems.isNotEmpty()) {
        daysForecastRowItems.forEach { daysForecastRowItems ->
            WeatherForecastRowItem(modifier, daysForecastRowItems)
        }
        daysForecastRowItems.clear()
    } else {
        FormatingOfAveragesAndDisplaying(packageLocationInducedWeatherViewModels)
        if (wasWeatherForecastSuccessful == true) {
            packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator.recordLocationGenerallyOrAsFavourite(currentUserCoordinates, false)
        }
        wasWeatherForecastSuccessful = null
    }
}

@Composable
fun FormatingOfAveragesAndDisplaying(packageLocationInducedWeatherViewModels: PackageLocationInducedWeatherViewModels) = with(packageLocationInducedWeatherViewModels.locationInducedViewModel) {
    var daysOfTheWeekIndex = 0
    val periodWeatherConditions = locationInducedForecastWeatherResponse.periodWeatherConditions
    val viewModelsCoordinator = packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator
    periodWeatherConditions.forEachIndexed { index, periodWeatherCondition ->
        val dayOfTheWeek = viewModelsCoordinator.convertTimestampIntoDayOfTheWeek(periodWeatherCondition.timeOfDataForecast)
        val isNextForecastOnADifferentDay = index + 1 <= periodWeatherConditions.size - 1 && dayOfTheWeek != viewModelsCoordinator.convertTimestampIntoDayOfTheWeek(periodWeatherConditions[index + 1].timeOfDataForecast)

        if (isNextForecastOnADifferentDay || (index == periodWeatherConditions.size - 1 &&  daysOfTheWeekIndex < 5)) {
            WeatherForecastRowItem(
                modifier,
                viewModelsCoordinator.getRowItemAveragesForDayForecast(daysOfTheWeekIndex, periodWeatherCondition, dayOfTheWeek, index)
            )
            daysOfTheWeekIndex++
        } else {
            packageLocationInducedWeatherViewModels.locationInducedWeatherViewModelCoordinator.addValuesToMutableList(dayOfTheWeek, periodWeatherCondition)
        }
    }
}

@Composable
fun LowerScreenSetUp(modifier: Modifier, locationInducedCurrentWeatherResponse: LocationInducedCurrentWeatherResponse) {
    val weatherNumericalSummary = locationInducedCurrentWeatherResponse.weatherConditionsNumericalSummary
    CurrentWeatherRowItem(modifier, RowItemValues(
            minTemperature = "${weatherNumericalSummary.minimumTemperature.toInt()}$DEGREE_CHARACTER",
            currentTemperature = "${weatherNumericalSummary.temperature.toInt()}$DEGREE_CHARACTER",
            maxTemperature = "${weatherNumericalSummary.maximumTemperature.toInt()}$DEGREE_CHARACTER",
            textStyle = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    )
    CurrentWeatherRowItem(modifier, RowItemValues(
            minTemperature = stringResource(R.string.min_temperature),
            currentTemperature = stringResource(R.string.current_temperature),
            maxTemperature = stringResource(R.string.max_temperature),
            textStyle = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Normal
        )
    )
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
            modifier = modifier.weight(1f)
                .padding(end = dimensionResource(R.dimen.dimension_8dp)),
            text = maxTemperature,
            textAlign = TextAlign.End,
            style = textStyle,
            fontWeight = fontWeight
        )
    }
}

@Composable
fun RequestLocationPermissionAndPopulateStateFlow(locationInducedViewModel: LocationInducedViewModel, rememberSaveAblePassObjectForLoading: RememberSaveAblePassObject, onLocationPermissionGrantedMutableState: () -> Unit) {
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
            rememberSaveAblePassObjectForLoading.setSaveAbleStateFlowToFalse
            locationInducedViewModel.setShowLocationPermissionDescriptionDialog(true)
        }
    })

    LaunchedEffect(Unit) {
        locationInducedViewModel.permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}