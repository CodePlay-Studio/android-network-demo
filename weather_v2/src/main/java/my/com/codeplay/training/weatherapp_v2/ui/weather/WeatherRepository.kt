package my.com.codeplay.training.weatherapp_v2.ui.weather

import my.com.codeplay.training.weatherapp_v2.datasource.MockWeatherRemoteDataSource
import my.com.codeplay.training.weatherapp_v2.datasource.WeatherLocalDataSource
import my.com.codeplay.training.weatherapp_v2.datasource.WeatherRemoteDataSource
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather
import my.com.codeplay.training.weatherapp_v2.remote.model.WeatherApiData
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val mockWeatherRemoteDataSource: MockWeatherRemoteDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource
    ) {

    suspend fun getWeatherData(id: Int): WeatherApiData {
        val data =
            /*if (BuildConfig.DEBUG)
                mockWeatherRemoteDataSource.getWeatherData(id)
            else*/
                weatherRemoteDataSource.getWeatherData(id)
        return data
    }

    suspend fun saveWeatherData(record: Weather) = weatherLocalDataSource.insert(record)
}