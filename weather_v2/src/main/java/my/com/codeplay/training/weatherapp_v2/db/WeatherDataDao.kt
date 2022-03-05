package my.com.codeplay.training.weatherapp_v2.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather

@Dao
interface WeatherDataDao {

    @Query("SELECT * FROM weather")
    fun getAll(): List<Weather>

    @Insert
    fun insert(weatherData: Weather)
}