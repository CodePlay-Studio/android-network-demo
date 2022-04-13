package my.com.codeplay.training.weatherapp_v2.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import my.com.codeplay.training.weatherapp_v2.db.DatabaseManager
import my.com.codeplay.training.weatherapp_v2.db.WeatherDataDao

@Module
@InstallIn(ViewModelComponent::class)
object DataModule {

    @Provides
    fun provideWeatherDAO(@ApplicationContext context: Context): WeatherDataDao {
        return DatabaseManager.getInstance(context).weatherDataDao()
    }
}