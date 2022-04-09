package my.com.codeplay.training.weatherapp_v2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import my.com.codeplay.training.weatherapp_v2.remote.RetrofitServiceManager
import my.com.codeplay.training.weatherapp_v2.remote.model.Data
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {
    /* Step 1 - without LiveData
    var data : Data? = null
    var errMsg : String? = null

    fun requestWeahtherUpdate(cityId: Int, callback: (Data?, String?) -> Unit) {
        RetrofitServiceManager.weatherService.getWeatherData(cityId).enqueue(
            object: Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        data = response.body()
                    } else {
                        data = null
                        errMsg = response.message()
                    }
                    callback(data, errMsg)
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    data = null
                    errMsg = t.message
                    callback(data, errMsg)
                }
            }
        )
    }
    // */

    private val _data: MutableLiveData<Data> = MutableLiveData()
    val data: LiveData<Data> get() = _data
    private val _errMsg: MutableLiveData<String> = MutableLiveData("")
    val errMsg: LiveData<String> get() = _errMsg

    fun requestWeahtherUpdate(cityId: Int) {
        RetrofitServiceManager.weatherService.getWeatherData(cityId).enqueue(
            object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        _data.value = response.body()
                    } else {
                        _errMsg.value = response.message()
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    _errMsg.value = t.message
                }
            }
        )
    }

    fun notifyErrMsgPrompted() {
        _errMsg.value = ""
    }
}