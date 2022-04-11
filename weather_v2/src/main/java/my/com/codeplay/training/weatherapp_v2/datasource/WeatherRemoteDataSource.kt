package my.com.codeplay.training.weatherapp_v2.datasource

import my.com.codeplay.training.weatherapp_v2.remote.RetrofitServiceManager

class WeatherRemoteDataSource {
    suspend fun getWeatherData(id: Int) = RetrofitServiceManager.weatherService.getWeatherData(id)
}