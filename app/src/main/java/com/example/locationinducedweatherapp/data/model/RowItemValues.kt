package com.example.locationinducedweatherapp.data.model

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

data class RowItemValues (
    var minTemperature: String = "",
    var currentTemperature: String = "",
    var maxTemperature: String = "",
    var imageResourceIdentifier: Int = -1,
    var dayOfTheWeek: String = "",
    val textStyle: TextStyle,
    val fontWeight: FontWeight = FontWeight.Normal
)