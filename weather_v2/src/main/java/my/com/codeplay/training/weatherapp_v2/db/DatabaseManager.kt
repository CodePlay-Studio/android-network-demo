package my.com.codeplay.training.weatherapp_v2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather

@Database(entities = [Weather::class], version = 1)
abstract class DatabaseManager: RoomDatabase() {
    abstract fun weatherDataDao(): WeatherDataDao

    companion object {
        private const val DATABASE_NAME = "Weather.db"
        private var instance: DatabaseManager? = null

        fun getInstance(context: Context): DatabaseManager {
            synchronized(this) {
                var localInstance: DatabaseManager? = instance

                if (localInstance == null) {
                    localInstance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseManager::class.java,
                        DATABASE_NAME
                    ).build()

                    instance = localInstance
                }
                return localInstance
            }
        }
    }
}