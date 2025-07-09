package com.example.locationinducedweatherapp.data.api

sealed class APIResponseHandler<T> (
    var successResponse: T? = null,
    var failureMessage: String? = null
) {
    class Success<T>(successResponse: T?): APIResponseHandler<T>(successResponse)
    class Failure<T>(successResponse: T? = null, failureMessage: String): APIResponseHandler<T>(successResponse, failureMessage)
}