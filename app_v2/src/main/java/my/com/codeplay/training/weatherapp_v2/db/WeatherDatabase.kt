package my.com.codeplay.training.weatherapp_v2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather

@Database(entities = arrayOf(Weather::class), version = 2)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun weatherDataDao(): WeatherDataDao

    companion object {
        private const val DATABASE_NAME = "Weather.db"
        private var instance: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}