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
        // Annotate with @Volatile to make the database instance variable never be cached
        // in CPU caches, and all writes and reads will be done to and from the main memory.
        // This makes sure the value of instance is always up-to-date and the same to all
        // execution threads.
        @Volatile
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