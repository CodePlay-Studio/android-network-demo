package my.com.codeplay.training.weatherapp_v2.remote

import my.com.codeplay.training.weatherapp_v2.remote.model.WeatherApiData
import my.com.codeplay.training.weatherapp_v2.remote.model.Forecast
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather?appid=82445b6c96b99bc3ffb78a4c0e17fca5&mode=json&units=metric")
    suspend fun getWeatherData(@Query("id") id: Int): WeatherApiData

    @GET("data/2.5/onecall?appid=82445b6c96b99bc3ffb78a4c0e17fca5&units=metric&exclude=minutely,hourly,alerts")
    fun get7DaysForecast(@Query("lon") lon: Float, @Query("lat") lat: Float): Call<Forecast>
}