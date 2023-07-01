package com.example.weathercompose.repository

import com.example.weathercompose.data.remote.WeatherApi
import com.example.weathercompose.data.remote.responses.Weather
import com.example.weathercompose.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.internal.http2.Http2Reader.Companion.logger
import javax.inject.Inject

@ActivityScoped
class WeatherRepository @Inject constructor(
    private val api: WeatherApi
) {
    suspend fun getWeatherData(apiKey:String, city: String): Resource<Weather>{
        val response = try {
            api.getWeatherData(apiKey, city)
        } catch (e: Exception) {
            return Resource.Error(e.toString())
        }
        return Resource.Success(response)
    }
}