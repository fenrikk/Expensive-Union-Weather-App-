package com.milescott.expensiveunion

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface WeatherApi {
    @GET("./v2.0/current?lat=80.227673&lon=14.014661&key=fd648c1cdb014e759086abea81e377dc")
    fun getWeather(): Single<Response>
}

data class Response(
    val data: List<WeatherData>
)

data class WeatherData(
    val wind_cdir_full: String,
    val wind_spd: Float,
    val sunset: String,
    val snow: Float,
    val uv: Float,
    val weather: Weather,
    val sunrise: String,
    val temp: Float
)

data class Weather(
    val description: String
)