package com.example.locationinducedweatherapp.dependencyInjection

import android.app.Application
import com.example.locationinducedweatherapp.util.Constants
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocationInducedApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, Constants.GOOGLE_MAP_API_KEY)
        }
    }
}