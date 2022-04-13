package my.com.codeplay.training.weatherapp_v2.ui.weather

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import my.com.codeplay.training.weatherapp_v2.datasource.MockWeatherRemoteDataSource
import my.com.codeplay.training.weatherapp_v2.datasource.WeatherLocalDataSource
import my.com.codeplay.training.weatherapp_v2.datasource.WeatherRemoteDataSource
import my.com.codeplay.training.weatherapp_v2.db.DatabaseManager
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather
import my.com.codeplay.training.weatherapp_v2.remote.model.WeatherApiData
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    application: Application
) : AndroidViewModel(application) {
//    private val repository = WeatherRepository(
//        WeatherRemoteDataSource(),
//        MockWeatherRemoteDataSource(),
//        WeatherLocalDataSource(DatabaseManager.getInstance(application.applicationContext).weatherDataDao())
//    )

    private val _weatherApiData: MutableLiveData<WeatherApiData> = MutableLiveData()
    val weatherData: LiveData<Weather> get() = Transformations.map(_weatherApiData) { apiData ->
        with(apiData) {
            Weather(
                name,
                weather.first().description,
                main.temp.toString(),
                wind.speed.toString(),
                main.humidity.toString(),
                clouds.all.toString(),
                getWeatherIcon(weather.first().id),
                datetime
            )
        }.also {
            viewModelScope.launch {
                repository.saveWeatherData(it)
            }
        }
    }
    private val _errMsg: MutableLiveData<String> = MutableLiveData("")
    val errMsg: LiveData<String> get() = _errMsg
    private val _isProcessing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isProcessing: LiveData<Boolean> get() = _isProcessing

    fun requestWeatherUpdate(cityId: Int) = viewModelScope.launch {
        try {
            _isProcessing.value = true

            val weatherApiData = repository.getWeatherData(cityId)
            _weatherApiData.value = weatherApiData
        } catch (e: Exception) {
            _errMsg.value = e.message
        } finally {
            _isProcessing.value = false
        }
    }

    fun notifyErrMsgPrompted() {
        _errMsg.value = ""
    }
}