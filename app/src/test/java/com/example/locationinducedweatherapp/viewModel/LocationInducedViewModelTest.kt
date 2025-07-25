package com.example.locationinducedweatherapp.viewModel

import android.app.Application
import android.content.Context
import androidx.navigation.NavHostController
import com.example.locationinducedweatherapp.repository.weather.LocationInducedWeatherRepository
import com.example.locationinducedweatherapp.util.FailureTypeEnum
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LocationInducedWeatherViewModelTest {
    private lateinit var locationInducedWeatherViewModel: LocationInducedViewModel
    private lateinit var locationInducedWeatherRepository: LocationInducedWeatherRepository
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)
        val appContext: Context = mockk(relaxed = true)
        val application: Application = mockk(relaxed = true) {
            every { applicationContext } returns appContext
        }
        locationInducedWeatherRepository = mockk()
        locationInducedWeatherViewModel = LocationInducedViewModel(locationInducedWeatherRepository, application)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun verifyNavigationToLocationInducedWeatherReportScreen() {
        //Given
        val mockNavigationController = mockk<NavHostController>()
        every { mockNavigationController.navigate(any<String>()) } just runs
        locationInducedWeatherViewModel.navigationController = mockNavigationController

        //When
        locationInducedWeatherViewModel.navigateToLocationInducedWeatherReportScreen()

        //Then
        verify(exactly = 1) { locationInducedWeatherViewModel.navigationController.navigate(any<String>()) }
    }

    @Test
    fun verifyNavigationToViewFavouriteLocationProfilesScreen() {
        //Given
        val mockNavigationController = mockk<NavHostController>()
        every { mockNavigationController.navigate(any<String>()) } just runs
        locationInducedWeatherViewModel.navigationController = mockNavigationController

        //When
        locationInducedWeatherViewModel.navigateToViewFavouriteLocationProfilesScreen()

        //Then
        verify(exactly = 1) { locationInducedWeatherViewModel.navigationController.navigate(any<String>()) }
    }

    @Test
    fun verifyNavigationToViewAllFavouriteLocationsInGoogleMapsScreen() {
        //Given
        val mockNavigationController = mockk<NavHostController>()
        every { mockNavigationController.navigate(any<String>()) } just runs
        locationInducedWeatherViewModel.navigationController = mockNavigationController

        //When
        locationInducedWeatherViewModel.navigateToViewAllFavouriteLocationsInGoogleMapsScreen()

        //Then
        verify(exactly = 1) { locationInducedWeatherViewModel.navigationController.navigate(any<String>()) }
    }

    @Test
    fun verifyNavigationToViewUserGooglePlacesScreen() {
        //Given
        val mockNavigationController = mockk<NavHostController>()
        every { mockNavigationController.navigate(any<String>()) } just runs
        locationInducedWeatherViewModel.navigationController = mockNavigationController

        //When
        locationInducedWeatherViewModel.navigateToViewUserGooglePlacesScreen()

        //Then
        verify(exactly = 1) { locationInducedWeatherViewModel.navigationController.navigate(any<String>()) }
    }

    @Test
    fun verifyNavigationToNavigateToLocationInducedWeatherFailureScreenAndAssertWeatherAPISuccessfulFlagHasBeenUpdated() = runTest {
        //Given
        val mockNavigationController = mockk<NavHostController>()
        every { mockNavigationController.navigate(any<String>()) } just runs
        locationInducedWeatherViewModel.navigationController = mockNavigationController

        //When
        locationInducedWeatherViewModel.navigateToLocationInducedWeatherFailureScreen()

        //Then
        verify(exactly = 1) { locationInducedWeatherViewModel.navigationController.navigate(any<String>()) }
        assertEquals(locationInducedWeatherViewModel.weatherAPISuccessfulFlag.value, -1)
    }

    @Test
    fun assertAndVerifyCallsForGoogleServicesResponseHandleInCaseThereAreValidCoordinates() = with(locationInducedWeatherViewModel) {
        //Given
        val mockNavigationController = mockk<NavHostController>()
        every { mockNavigationController.navigate(any<String>()) } just runs
        navigationController = mockNavigationController
        val googlePlacesResult = "-11.19; 77.43"

        //When
        googleServicesResponseHandle(googlePlacesResult)

        //Then
        assertEquals(isInvocationFromGooglePlaces, true)
        assertEquals(searchPlaceInGoogle?.latitude, googlePlacesResult.split(";").first().toDouble())
        assertEquals(searchPlaceInGoogle?.longitude, googlePlacesResult.split(";").last().toDouble())
        verify(exactly = 1) { locationInducedWeatherViewModel.navigationController.navigate(any<String>()) }

    }

    @Test
    fun assertAndVerifyCallsForGoogleServicesResponseHandleInCaseTheCoordinatesAreInvalid() = with(locationInducedWeatherViewModel) {
        //Given
        val mockNavigationController = mockk<NavHostController>()
        every { mockNavigationController.navigate(any<String>()) } just runs
        navigationController = mockNavigationController
        val googlePlacesResult = "Error"

        //When
        googleServicesResponseHandle(googlePlacesResult)

        //Then
        assertEquals(failureResponse.failureType, FailureTypeEnum.GeneralErrorFailures)
        assertEquals(failureResponse.failureMessage, googlePlacesResult)
        verify(exactly = 1) { locationInducedWeatherViewModel.navigationController.navigate(any<String>()) }
    }
}