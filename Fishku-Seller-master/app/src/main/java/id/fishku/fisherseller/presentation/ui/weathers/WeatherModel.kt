package id.fishku.fisherseller.presentation.ui.weathers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.fishku.fisherseller.retrofit.ApiConfig
import id.fishku.fisherseller.seller.services.SessionManager

class WeatherModel(context: Context) : ViewModel() {

    private val _weather = MutableLiveData<CurrentWeather>()
    val weather: LiveData<CurrentWeather> = _weather


    private val prefs: SessionManager = SessionManager(context)

    init {
        getWeather()
    }


    private fun getWeather() {
        val client = ApiConfig.getApiService().getWeather(
            prefs.getLatitude().toDouble(),
            prefs.getLongitude().toDouble(),
            true,
            listOf("temperature_2m", "relativehumidity_2m", "windspeed_10m")
        )

        client.enqueue(object : retrofit2.Callback<WeatherResponse> {
            override fun onResponse(
                call: retrofit2.Call<WeatherResponse>,
                response: retrofit2.Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    _weather.value = response.body()?.currentWeather
                }
            }

            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }
}