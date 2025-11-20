package com.example.healthtracker.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

object WeatherApi {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                json = kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }


    @Serializable
    data class WeatherResponse(
        val current_weather: CurrentWeather?
    )

    @Serializable
    data class CurrentWeather(
        val time: String,
        val interval: Int,
        val temperature: Float,
        val windspeed: Float,
        val winddirection: Int,
        val is_day: Int,
        val weathercode: Int
    )

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        return client.get(
            "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
        ).body()
    }
}
