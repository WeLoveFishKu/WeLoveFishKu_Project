package id.fishku.fisherseller.presentation.ui.weathers

import com.google.gson.annotations.SerializedName

data class WeatherResponse(

	@field:SerializedName("elevation")
	val elevation: Any,

	@field:SerializedName("hourly_units")
	val hourlyUnits: HourlyUnits,

	@field:SerializedName("generationtime_ms")
	val generationtimeMs: Any,

	@field:SerializedName("timezone_abbreviation")
	val timezoneAbbreviation: String,

	@field:SerializedName("timezone")
	val timezone: String,

	@field:SerializedName("latitude")
	val latitude: Double,

	@field:SerializedName("utc_offset_seconds")
	val utcOffsetSeconds: Int,

	@field:SerializedName("hourly")
	val hourly: Hourly,

	@field:SerializedName("current_weather")
	val currentWeather: CurrentWeather,

	@field:SerializedName("longitude")
	val longitude: Double
)

data class Hourly(

	@field:SerializedName("temperature_2m")
	val temperature2m: List<Any>,

	@field:SerializedName("relativehumidity_2m")
	val relativehumidity2m: List<Int>,

	@field:SerializedName("windspeed_10m")
	val windspeed10m: List<Any>,

	@field:SerializedName("time")
	val time: List<String>
)

data class HourlyUnits(

	@field:SerializedName("temperature_2m")
	val temperature2m: String,

	@field:SerializedName("relativehumidity_2m")
	val relativehumidity2m: String,

	@field:SerializedName("windspeed_10m")
	val windspeed10m: String,

	@field:SerializedName("time")
	val time: String
)

data class CurrentWeather(

	@field:SerializedName("weathercode")
	val weathercode: Int,

	@field:SerializedName("temperature")
	val temperature: Any,

	@field:SerializedName("windspeed")
	val windspeed: Any,

	@field:SerializedName("is_day")
	val isDay: Int,

	@field:SerializedName("time")
	val time: String,

	@field:SerializedName("winddirection")
	val winddirection: Any
)
