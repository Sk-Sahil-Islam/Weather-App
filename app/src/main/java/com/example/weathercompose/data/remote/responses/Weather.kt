package com.example.weathercompose.data.remote.responses

data class Weather(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)