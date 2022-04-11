package my.com.codeplay.training.weatherapp_v2.datasource

import my.com.codeplay.training.weatherapp_v2.db.WeatherDataDao
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather

class WeatherLocalDataSource(private val weatherDataDao: WeatherDataDao) {
    suspend fun insert(record: Weather) = weatherDataDao.insert(record)
}