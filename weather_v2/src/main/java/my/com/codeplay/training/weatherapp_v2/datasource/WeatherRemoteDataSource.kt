package my.com.codeplay.training.weatherapp_v2.datasource

import my.com.codeplay.training.weatherapp_v2.remote.RetrofitServiceManager
import javax.inject.Inject

class WeatherRemoteDataSource  @Inject constructor() {
    suspend fun getWeatherData(id: Int) = RetrofitServiceManager.weatherService.getWeatherData(id)
}