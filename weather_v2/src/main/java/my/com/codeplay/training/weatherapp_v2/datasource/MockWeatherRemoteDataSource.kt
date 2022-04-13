package my.com.codeplay.training.weatherapp_v2.datasource

import my.com.codeplay.training.weatherapp_v2.remote.model.WeatherApiData
import javax.inject.Inject

class MockWeatherRemoteDataSource @Inject constructor() {
    suspend fun getWeatherData(id: Int): WeatherApiData = TODO("Not yet implemented")
}