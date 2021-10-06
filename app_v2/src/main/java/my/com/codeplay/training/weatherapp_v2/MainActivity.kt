package my.com.codeplay.training.weatherapp_v2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import my.com.codeplay.training.weatherapp_v2.databinding.ActivityMainBinding
import my.com.codeplay.training.weatherapp_v2.db.WeatherDatabase
import my.com.codeplay.training.weatherapp_v2.remote.model.Data
import my.com.codeplay.training.weatherapp_v2.db.entity.Weather
import my.com.codeplay.training.weatherapp_v2.remote.RetrofitServiceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val location = "location"
    private val temperature = "temperature"
    private val windSpeed = "windSpeed"
    private val humidity = "humidity"
    private val cloudiness = "cloudiness"
    private val iconId = "icon"

    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        //sharedPreferences = getSharedPreferences("MyAppSettings", Context.MODE_PRIVATE)

        binding.buttonRefresh.setOnClickListener {
            RetrofitServiceManager.retrofitService.getWeatherData(1735161).enqueue(
                object: Callback<Data> {
                    override fun onResponse(call: Call<Data>, response: Response<Data>) {
                        if (response.isSuccessful) {
                            val data = response.body() as Data
                            binding.location.text = data.name
                            binding.temperature.text = data.main.temp.toInt().toString()
                            binding.windSpeed.text = data.wind.speed.toString()
                            binding.humidity.text = data.main.humidity.toString()
                            binding.cloudiness.text = data.clouds.all.toString()

                            data.weather.firstOrNull()?.let {
                                val resId = when (it.id) {
                                    //200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> R.drawable.ic_thunderstorm_large
                                    //300, 301, 302, 310, 311, 312, 313, 314, 321 -> R.drawable.ic_drizzle_large
                                    //500, 501, 502, 503, 504, 511, 520, 521, 522, 531 -> R.drawable.ic_rain_large
                                    //600, 601, 602, 611, 612, 615, 616, 620, 621, 622 -> R.drawable.ic_snow_large
                                    in 200..232 -> R.drawable.ic_thunderstorm_large
                                    in 300..321 -> R.drawable.ic_drizzle_large
                                    in 500..531 -> R.drawable.ic_rain_large
                                    in 600..622 -> R.drawable.ic_snow_large
                                    800 -> R.drawable.ic_day_clear_large
                                    801 -> R.drawable.ic_day_few_clouds_large
                                    802 -> R.drawable.ic_scattered_clouds_large
                                    803, 804 -> R.drawable.ic_broken_clouds_large
                                    701, 711, 721, 731, 741, 751, 761, 762 -> R.drawable.ic_fog_large
                                    781, 900 -> R.drawable.ic_tornado_large
                                    905 -> R.drawable.ic_windy_large
                                    906 -> R.drawable.ic_hail_large
                                    else -> R.mipmap.ic_launcher
                                }
                                binding.icon.setImageResource(resId)

                                sharedPreferences.edit()
                                    .putString(location, data.name)
                                    .putString(temperature, data.main.temp.toInt().toString())
                                    .putString(windSpeed, data.wind.speed.toString())
                                    .putString(humidity, data.main.humidity.toString())
                                    .putString(cloudiness, data.clouds.all.toString())
                                    .putInt(iconId, resId)
                                    .apply()

                                Thread(
                                    object : Runnable {
                                        override fun run() {
                                            WeatherDatabase.getInstance(this@MainActivity).weatherDataDao().insert(
                                                Weather(
                                                    data.name,
                                                    data.main.temp.toInt().toString(),
                                                    data.wind.speed.toString(),
                                                    data.main.humidity.toString(),
                                                    data.clouds.all.toString(),
                                                    resId
                                                )
                                            )
                                        }
                                    }
                                ).start()
                            }
                        } else {
                            Snackbar.make(view, response.message() ?: "Unknown Error", Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<Data>, t: Throwable) {
                        Snackbar.make(view, t.message ?: "Unknown Error", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            )
        }

        binding.buttonHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.location.text = sharedPreferences.getString(location, "-")
        binding.temperature.text = sharedPreferences.getString(temperature, "")
        binding.windSpeed.text = sharedPreferences.getString(windSpeed, "0.00")
        binding.humidity.text = sharedPreferences.getString(humidity, "0")
        binding.cloudiness.text = sharedPreferences.getString(cloudiness, "0")
        binding.icon.setImageResource(sharedPreferences.getInt(iconId, R.mipmap.ic_launcher))

        val isShown = sharedPreferences.getBoolean("hasShown", false)
        if (!isShown) {
            Snackbar.make(view, "Welcome and thank you for installing Weather app", Snackbar.LENGTH_LONG)
                .show()

            sharedPreferences.edit()
                .putBoolean("hasShown", true)
                .apply()
        }
    }
}