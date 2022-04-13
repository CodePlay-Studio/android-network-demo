package my.com.codeplay.training.weatherapp_v2.datasource

import my.com.codeplay.training.weatherapp_v2.db.WeatherDataDao
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather
import javax.inject.Inject

class WeatherLocalDataSource @Inject constructor(private val weatherDataDao: WeatherDataDao) {
    suspend fun insert(record: Weather) = weatherDataDao.insert(record)
}