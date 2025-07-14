package com.example.locationinducedweatherapp.data.api

import com.example.locationinducedweatherapp.data.model.FailureResponse
import com.example.locationinducedweatherapp.util.FailureTypeEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

class APIConfigurations {
    fun <T> populateResponseFromWeatherAPI(isWeatherAPI: Boolean = true, apiFunctionCall: suspend () -> Response<T>): Flow<APIResponseHandler<T>> {
        lateinit var customWeatherResponse: Response<T>
        return flow {
            try {
                customWeatherResponse = apiFunctionCall()
                if (!customWeatherResponse.isSuccessful) {
                    customWeatherResponse.errorBody()?.string().let { errorBody ->
                        emit(APIResponseHandler.Failure(failureResponse = FailureResponse(failureType = FailureTypeEnum.GeneralErrorFailures, failureMessage = errorBody.toString())))
                    }
                    return@flow
                }
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                val networkErrorMessage = e.message ?: ""
                emit(APIResponseHandler.Failure(failureResponse = FailureResponse(failureType = FailureTypeEnum.Internet, failureMessage = networkErrorMessage, isWeatherAPI = isWeatherAPI)))
                return@flow
            }
            catch (e: IOException) {
                e.printStackTrace()
                val networkErrorMessage = e.message ?: ""
                emit(APIResponseHandler.Failure(failureResponse = FailureResponse(failureType = FailureTypeEnum.GeneralNetwork, failureMessage = networkErrorMessage)))
                return@flow
            }

            catch (e: Exception) {
                e.printStackTrace()
                val networkErrorMessage = e.message ?: ""
                emit(APIResponseHandler.Failure(failureResponse = FailureResponse(failureType = FailureTypeEnum.GeneralErrorFailures, failureMessage = networkErrorMessage)))
                return@flow
            }
            emit(APIResponseHandler.Success(successResponse = customWeatherResponse.body()))
        }
    }
}