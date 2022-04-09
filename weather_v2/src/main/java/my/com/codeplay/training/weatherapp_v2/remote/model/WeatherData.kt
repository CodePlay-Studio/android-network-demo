package my.com.codeplay.training.weatherapp_v2.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    val name: String,
    val main: Main,
    val wind: Wind,
    val clouds: Clouds,
    val weather: List<Element>
)

@JsonClass(generateAdapter = true)
data class Wind(
    val speed: Double,
    val deg: Int
)

@JsonClass(generateAdapter = true)
data class Main(
    val temp: Double,
    val humidity: Int
)

@JsonClass(generateAdapter = true)
data class Clouds(
    val all: Int
)

@JsonClass(generateAdapter = true)
data class Element(
    val id: Int,
    val description: String
)
