package id.fishku.fisherseller.presentation.ui.weathers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.fishku.fisherseller.retrofit.ApiConfig

class WeatherModel(context: Context) : ViewModel() {

    private val _weather = MutableLiveData<CurrentWeather>()
    val weather: LiveData<CurrentWeather> = _weather

    private val _hourly = MutableLiveData<HourlyWave>()
    val hourly: LiveData<HourlyWave> = _hourly

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        getWeather()
    }


    fun getWeather(lat: Double = -6.200000, long: Double = 106.816666) {
        if (lat != null && long != null) {
            _loading.value = true
            val client = ApiConfig.getApiService().getWeather(
                lat,
                long,
                true,
                listOf("temperature_2m", "relativehumidity_2m", "windspeed_10m")
            )

            client.enqueue(object : retrofit2.Callback<WeatherResponse> {
                override fun onResponse(
                    call: retrofit2.Call<WeatherResponse>,
                    response: retrofit2.Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        _loading.value = false
                        _weather.value = response.body()?.currentWeather
                    }
                }

                override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }


    fun getHourly(lat: Double, long: Double) {
        if (lat != null && long != null) {
            _loading.value = true
            val client = ApiConfig.getWaveApiService().getWave(
                lat,
                long,
                listOf("wave_height"),
                "metric"
            )

            client.enqueue(object : retrofit2.Callback<WaveResponse> {
                override fun onResponse(
                    call: retrofit2.Call<WaveResponse>,
                    response: retrofit2.Response<WaveResponse>
                ) {
                    if (response.isSuccessful) {
                        _loading.value = false
                        _hourly.value = response.body()?.hourly
                    }
                }

                override fun onFailure(call: retrofit2.Call<WaveResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }

    }
}