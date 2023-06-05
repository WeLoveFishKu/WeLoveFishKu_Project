package id.fishku.fisherseller.presentation.ui.weathers

import com.google.gson.annotations.SerializedName

data class WaveResponse(

    @field:SerializedName("hourly_units")
    val hourlyUnits: HourlyUnitsWave,

    @field:SerializedName("generationtime_ms")
    val generationtimeMs: Any,

    @field:SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,

    @field:SerializedName("timezone")
    val timezone: String,

    @field:SerializedName("latitude")
    val latitude: Any,

    @field:SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,

    @field:SerializedName("hourly")
    val hourly: HourlyWave,

    @field:SerializedName("longitude")
    val longitude: Any
)

data class HourlyUnitsWave(

    @field:SerializedName("wave_height")
    val waveHeight: String,

    @field:SerializedName("time")
    val time: String
)

data class HourlyWave(

    @field:SerializedName("wave_height")
    val waveHeight: List<Double>,

    @field:SerializedName("time")
    val time: List<String>
)
