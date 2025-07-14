package com.example.locationinducedweatherapp.data.api

import com.example.locationinducedweatherapp.data.model.FailureResponse

sealed class APIResponseHandler<T> (
    var successResponse: T? = null,
    var failureResponse: FailureResponse? = null
) {
    class Success<T>(successResponse: T?): APIResponseHandler<T>(successResponse)
    class Failure<T>(successResponse: T? = null, failureResponse: FailureResponse): APIResponseHandler<T>(successResponse, failureResponse)
}