package my.com.codeplay.training.weatherapp_v2.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Forecast(
    val current: Current,
    val daily: List<Daily>
)

@JsonClass(generateAdapter = true)
data class Current(
    @Json(name = "dt") val datetime: Long,
    @Json(name = "temp") val temperature: Double,
    val humidity: Int,
    @Json(name = "clouds") val cloudiness: Int,
    @Json(name = "wind_speed") val windspeed: Double,
    val weather: List<Element>
)

@JsonClass(generateAdapter = true)
data class Daily(
    @Json(name = "dt") val datetime: Long,
    @Json(name = "temp") val temperature: Temperature,
    val humidity: Int,
    @Json(name = "wind_speed") val windspeed: Double,
    @Json(name = "clouds") val cloudiness: Int,
    val weather: List<Element>
)

@JsonClass(generateAdapter = true)
data class Temperature(
    val min: Double,
    val max: Double
)

