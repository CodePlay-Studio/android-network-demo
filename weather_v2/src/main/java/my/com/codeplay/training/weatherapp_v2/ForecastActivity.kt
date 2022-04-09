package my.com.codeplay.training.weatherapp_v2

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import my.com.codeplay.training.weatherapp_v2.databinding.ActivityForecastBinding
import my.com.codeplay.training.weatherapp_v2.databinding.ItemForecastBinding
import my.com.codeplay.training.weatherapp_v2.remote.RetrofitServiceManager
import my.com.codeplay.training.weatherapp_v2.remote.model.Daily
import my.com.codeplay.training.weatherapp_v2.remote.model.Forecast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForecastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = intent.getStringExtra(EXTRA_CITY_NAME)
        supportActionBar?.subtitle = getString(R.string._7_days_forecast)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val lon = intent.getFloatExtra(EXTRA_CITY_LON, 0.0f)
        val lat = intent.getFloatExtra(EXTRA_CITY_LAT, 0.0f)
        requestForecast(lon, lat)
    }

    private fun requestForecast(lon: Float, lat: Float) {
        binding.progress.isVisible = true

        RetrofitServiceManager.weatherService.get7DaysForecast(lon, lat).enqueue(
            object : Callback<Forecast> {
                override fun onResponse(call: Call<Forecast>, response: Response<Forecast>) {
                    binding.progress.isVisible = false

                    if (response.isSuccessful) {
                        response.body()?.let {
                            binding.forecastList.apply {
                                /*addItemDecoration(
                                    DividerItemDecoration(
                                        this@ForecastActivity,
                                        DividerItemDecoration.VERTICAL
                                    )
                                )*/
                                adapter = ForecastAdapter(it.daily)
                                setHasFixedSize(true)
                            }
                        }
                    } else {
                        Snackbar.make(
                            binding.root,
                            getString(
                                R.string.err_request_failed,
                                response.message() ?: "Unknown Error"
                            ),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Forecast>, t: Throwable) {
                    binding.progress.isVisible = false

                    Snackbar.make(
                        binding.root,
                        getString(
                            R.string.err_request_failed,
                            t.message ?: "Unknown Error"
                        ),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private class ForecastAdapter(
        val items: List<Daily>
    ) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ItemForecastBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val forecast = items[position]
            val weather = forecast.weather.first()
            with(holder.binding) {
                val iconResId = getWeatherIcon(weather.id)
                val cardColor = when (iconResId) {
                    R.drawable.ic_day_clear_large, R.drawable.ic_day_few_clouds_large -> R.color.weather_clear_sky
                    R.drawable.ic_scattered_clouds_large, R.drawable.ic_broken_clouds_large -> R.color.weather_cloudy
                    R.drawable.ic_drizzle_large, R.drawable.ic_rain_large -> R.color.weather_rain
                    R.drawable.ic_hail_large, R.drawable.ic_snow_large -> R.color.weather_hail_and_snow
                    else -> R.color.weather_cloudy
                }
                card.setCardBackgroundColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            card.context,
                            cardColor
                        )
                    )
                )
                icon.setImageResource(iconResId)
                dateText.text = if (position == 0) root.context.getString(R.string.today)
                                else DateFormat.getDateInstance().format(Date(forecast.datetime * 1000))
                weatherInfo.text = weather.description
                temperatureRangeText.text = root.context.getString(
                    R.string.temperature_range,
                    forecast.temperature.min,
                    forecast.temperature.max
                )
                windSpeedText.text = root.context.getString(R.string.wind_speed, forecast.windspeed)
                humidityText.text = root.context.getString(R.string.percent, forecast.humidity)
                cloudinessText.text = root.context.getString(R.string.percent, forecast.cloudiness)
            }
        }

        override fun getItemCount(): Int = items.size
    }
}