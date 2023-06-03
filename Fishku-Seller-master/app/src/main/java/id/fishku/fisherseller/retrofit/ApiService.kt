package id.fishku.fisherseller.retrofit

import id.fishku.fisherseller.presentation.ui.weathers.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("forecast")
    fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") current_weather: Boolean,
        @Query("hourly") hourly: List<String>,
    ): Call<WeatherResponse>
}