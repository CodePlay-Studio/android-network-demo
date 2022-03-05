package my.com.codeplay.training.weatherapp_v2.remote

import my.com.codeplay.training.weatherapp_v2.remote.model.Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather?APPID=82445b6c96b99bc3ffb78a4c0e17fca5&mode=json&units=metric")
    fun getWeatherData(@Query("id") id: Int): Call<Data>
}