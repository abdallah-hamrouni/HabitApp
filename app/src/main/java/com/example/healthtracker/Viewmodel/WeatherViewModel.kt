package com.example.healthtracker.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.network.WeatherApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weather = MutableStateFlow<WeatherApi.WeatherResponse?>(null)
    val weather: StateFlow<WeatherApi.WeatherResponse?> = _weather

    fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val result = WeatherApi.getWeather(lat, lon)
                println("🔵 WEATHER API RESULT: $result")
                _weather.value = result
            } catch (e: Exception) {
                println("❌ WEATHER ERROR: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}

