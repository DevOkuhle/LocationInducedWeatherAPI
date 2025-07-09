package com.example.locationinducedweatherapp.data.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class APIConfigurations {
    fun <T> populateResponseFromWeatherAPI(apiFunctionCall: suspend () -> Response<T>): Flow<APIResponseHandler<T>> {
        lateinit var customWeatherResponse: Response<T>
        return flow {
            try {
                customWeatherResponse = apiFunctionCall()
                if (!customWeatherResponse.isSuccessful) {
                    customWeatherResponse.errorBody()?.string().let { errorBody ->
                        emit(APIResponseHandler.Failure(failureMessage = errorBody.toString()))
                    }
                    return@flow
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val networkErrorMessage = e.message ?: ""
                emit(APIResponseHandler.Failure(failureMessage = networkErrorMessage))
                return@flow
            }
            emit(APIResponseHandler.Success(successResponse = customWeatherResponse.body()))
        }
    }
}