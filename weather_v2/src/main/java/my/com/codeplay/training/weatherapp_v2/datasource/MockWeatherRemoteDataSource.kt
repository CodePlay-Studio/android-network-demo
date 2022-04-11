package my.com.codeplay.training.weatherapp_v2.datasource

import my.com.codeplay.training.weatherapp_v2.remote.model.WeatherApiData

class MockWeatherRemoteDataSource {
    suspend fun getWeatherData(id: Int): WeatherApiData = TODO("Not yet implemented")
}