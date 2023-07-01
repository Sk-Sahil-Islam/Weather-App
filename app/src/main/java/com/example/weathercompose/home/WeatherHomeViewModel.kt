package com.example.weathercompose.home

import androidx.lifecycle.ViewModel
import com.example.weathercompose.data.remote.responses.Weather
import com.example.weathercompose.repository.WeatherRepository
import com.example.weathercompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherHomeViewModel @Inject constructor(
    private val repository: WeatherRepository
): ViewModel() {
    suspend fun getWeatherInfo(apiKey: String, city: String): Resource<Weather> {
        return repository.getWeatherData(apiKey = apiKey, city = city)
    }
}