package id.fishku.fisherseller.retrofit

import id.fishku.fishersellercore.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET

interface ApiService {

    @FormUrlEncoded
    @GET("forecast")
    fun getWeather(
        @Field("latitude") latitude: Float?,
        @Field("longitude") longitude: Float?,
        @Field("current_weather") current_weather: Boolean,
        @Field("hourly") hourly: List<String>,
    ): Call<WeatherResponse>
}