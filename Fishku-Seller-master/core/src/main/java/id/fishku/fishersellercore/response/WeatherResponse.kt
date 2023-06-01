package id.fishku.fishersellercore.response

import com.google.gson.annotations.SerializedName

data class WeatherResponse(

	@field:SerializedName("elevation")
	val elevation: Any? = null,

	@field:SerializedName("hourly_units")
	val hourlyUnits: HourlyUnits? = null,

	@field:SerializedName("generationtime_ms")
	val generationtimeMs: Any? = null,

	@field:SerializedName("timezone_abbreviation")
	val timezoneAbbreviation: String? = null,

	@field:SerializedName("timezone")
	val timezone: String? = null,

	@field:SerializedName("latitude")
	val latitude: Float? = null,

	@field:SerializedName("utc_offset_seconds")
	val utcOffsetSeconds: Int? = null,

	@field:SerializedName("hourly")
	val hourly: Hourly? = null,

	@field:SerializedName("current_weather")
	val currentWeather: Boolean? = null,

	@field:SerializedName("longitude")
	val longitude: Float? = null
)

data class Hourly(

	@field:SerializedName("temperature_2m")
	val temperature2m: List<Any?>? = null,

	@field:SerializedName("relativehumidity_2m")
	val relativehumidity2m: List<Int?>? = null,

	@field:SerializedName("windspeed_10m")
	val windspeed10m: List<Any?>? = null,

	@field:SerializedName("time")
	val time: List<String?>? = null
)

data class HourlyUnits(

	@field:SerializedName("temperature_2m")
	val temperature2m: String? = null,

	@field:SerializedName("relativehumidity_2m")
	val relativehumidity2m: String? = null,

	@field:SerializedName("windspeed_10m")
	val windspeed10m: String? = null,

	@field:SerializedName("time")
	val time: String? = null
)

data class CurrentWeather(

	@field:SerializedName("weathercode")
	val weathercode: Int? = null,

	@field:SerializedName("temperature")
	val temperature: Any? = null,

	@field:SerializedName("windspeed")
	val windspeed: Any? = null,

	@field:SerializedName("is_day")
	val isDay: Int? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("winddirection")
	val winddirection: Any? = null
)
