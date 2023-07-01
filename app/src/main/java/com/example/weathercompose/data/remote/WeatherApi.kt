package com.example.weathercompose.data.remote

import com.example.weathercompose.data.remote.responses.Weather
import com.example.weathercompose.util.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: String = "2",
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): Weather
}