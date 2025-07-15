package com.example.locationinducedweatherapp.data.model

import com.example.locationinducedweatherapp.util.FailureTypeEnum

data class FailureResponse (
    var failureType: FailureTypeEnum = FailureTypeEnum.GeneralErrorFailures,
    var failureMessage: String = ""
)