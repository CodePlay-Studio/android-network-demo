package my.com.codeplay.training.weatherapp_v2.ui.weather

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import my.com.codeplay.training.weatherapp_v2.*
import my.com.codeplay.training.weatherapp_v2.databinding.ActivityWeatherBinding
import my.com.codeplay.training.weatherapp_v2.ui.citylist.CityListActivity
import my.com.codeplay.training.weatherapp_v2.ui.citylist.cities
import my.com.codeplay.training.weatherapp_v2.ui.forecast.ForecastActivity
import my.com.codeplay.training.weatherapp_v2.ui.history.HistoryActivity

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val newCityId = it.data?.getIntExtra(EXTRA_CITY_ID, 0) ?: 0
            if (newCityId > 0) {
                if (cityId != newCityId) {
                    cityId = newCityId
                    cityName = it.data!!.getStringExtra(EXTRA_CITY_NAME)!!
                    cityLon = it.data!!.getFloatExtra(EXTRA_CITY_LON, 0.0f)
                    cityLat = it.data!!.getFloatExtra(EXTRA_CITY_LAT, 0.0f)

                    prefs.edit()
                        .putInt(KEY_CITY_ID, cityId)
                        .putString(KEY_LOCATION, cityName)
                        .putFloat(KEY_LONGITUDE, cityLon)
                        .putFloat(KEY_LATITUDE, cityLat)
                        .apply()

                    requestWeather(cityId)
                } else if (isOutdated()) {
                    requestWeather(cityId)
                }
            }
        }
    }
    private val cityListIntent by lazy {
        Intent(this, CityListActivity::class.java)
    }
    private lateinit var binding: ActivityWeatherBinding
    private lateinit var prefs: SharedPreferences
    private var cityId = 0
    private var cityName = "-"
    private var cityLon = 0.0f
    private var cityLat = 0.0f
    private var lastRecordTimestamp = 0L

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.locationButton.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                /* replace with single choice dialog
                startActivityForResult.launch(cityListIntent.also {
                    it.putExtra(EXTRA_CITY_ID, cityId)
                })
                // */
                promptSelectCityDialog(if (cityId > 0) cities.indexOfFirst { it.id == cityId } else 0)
            }
        }
        binding.menuButton.setOnClickListener {
            PopupMenu(this, it).apply {
                gravity = GravityCompat.END
                setForceShowIcon(true)
                setOnMenuItemClickListener(this@WeatherActivity)
                inflate(R.menu.weather_popup)
                show()
            }
        }

        prefs = getPreferences(Context.MODE_PRIVATE)
        cityId = prefs.getInt(KEY_CITY_ID, 0)

        if (cityId > 0) {
            cityName = prefs.getString(KEY_LOCATION, "-")!!
            lastRecordTimestamp = prefs.getLong(KEY_TIMESTAMP, 0)
            if (isOutdated()) {
                requestWeather(cityId)
            } else {
                updateWeather()
            }
        } else {
            binding.locationButton.text = getString(R.string.select_a_city)
            promptSelectCity()
        }

        viewModel.weatherData.observe(this) {
            // update last weather record in SharedPreferences
            prefs.edit().apply {
                lastRecordTimestamp = System.currentTimeMillis()
                putLong(KEY_TIMESTAMP, lastRecordTimestamp)

                putInt(KEY_WEATHER_ICON, it.iconId)
                putString(KEY_WEATHER_DESC, it.description)
                putString(KEY_LOCATION, it.location)
                putString(KEY_TEMPERATURE, it.temperature)
                putFloat(KEY_WIND_SPEED, it.windSpeed.toFloat())
                putInt(KEY_HUMIDITY, it.humidity.toInt())
                putInt(KEY_CLOUDINESS, it.cloudiness.toInt())
                apply()
            }

            updateWeather(
                it.iconId,
                it.description,
                it.location,
                it.temperature,
                it.windSpeed.toFloat(),
                it.humidity.toInt(),
                it.cloudiness.toInt()
            )
        }

        viewModel.errMsg.observe(this) {
            if (it.isEmpty())
                return@observe

            Snackbar.make(
                binding.root,
                getString(
                    R.string.err_request_failed,
                    it ?: "Unknown Error"
                ),
                Snackbar.LENGTH_LONG
            ).addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)

                    viewModel.notifyErrMsgPrompted()
                }
            }).show()
        }

        viewModel.isProcessing.observe(this) {
            binding.progress.isVisible = it
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_refresh -> {
                if (cityId == 0) {
                    promptSelectCity()
                    return true
                }

                if (binding.progress.isVisible.not()) {
                    if (isOutdated())
                        requestWeather(cityId)
                    else
                        Snackbar.make(binding.root, R.string.info_updated, Snackbar.LENGTH_SHORT)
                            .show()
                }
                true
            }
            R.id.action_open_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.action_forecast -> {
                if (cityId == 0) {
                    promptSelectCity()
                    return true
                }

                startActivity(Intent(this, ForecastActivity::class.java).apply {
                    putExtra(EXTRA_CITY_NAME, cityName)
                    putExtra(EXTRA_CITY_LON, cityLon)
                    putExtra(EXTRA_CITY_LAT, cityLat)
                })
                true
            }
            else -> false
        }
    }

    private fun promptSelectCity() = Snackbar.make(binding.root, R.string.select_a_city, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.set) {
            //startActivityForResult.launch(cityListIntent)
            promptSelectCityDialog()
        }
        .show()

    private fun promptSelectCityDialog(checkedItem: Int = 0)
        = AlertDialog.Builder(this, R.style.WeatherAlertDialog)
        .setTitle(R.string.select_a_city)
        .setSingleChoiceItems(cities.map { it.name }.toTypedArray(), checkedItem) { dialog, position ->
            with(cities[position]) {
                when {
                    cityId != id -> {
                        cityId = id
                        cityName = name
                        cityLon = lon
                        cityLat = lat

                        prefs.edit()
                            .putInt(KEY_CITY_ID, cityId)
                            .putString(KEY_LOCATION, cityName)
                            .putFloat(KEY_LONGITUDE, cityLon)
                            .putFloat(KEY_LATITUDE, cityLat)
                            .apply()

                        requestWeather(cityId)
                    }
                    isOutdated() -> requestWeather(cityId)
                    else -> { /* do nothing */ }
                }
            }
            dialog.dismiss()
        }
        .setPositiveButton(android.R.string.cancel, null)
        .create()
        .show()

    private fun isOutdated() = lastRecordTimestamp==0L
            || System.currentTimeMillis()-lastRecordTimestamp > INTERVAL_IN_MILLIS

    private fun requestWeather(cityId: Int) = viewModel.requestWeatherUpdate(cityId)

    private fun updateWeather(
        iconResId: Int = prefs.getInt(KEY_WEATHER_ICON, R.drawable.ic_unknown_large),
        description: String = prefs.getString(KEY_WEATHER_DESC, "Unknown")!!,
        location: String = prefs.getString(KEY_LOCATION, "-")!!,
        temperature: String = prefs.getString(KEY_TEMPERATURE, "-")!!,
        windspeed: Float = prefs.getFloat(KEY_WIND_SPEED, 0.0f),
        humidity: Int = prefs.getInt(KEY_HUMIDITY, 0),
        cloudiness: Int = prefs.getInt(KEY_CLOUDINESS, 0)
    ) {
        binding.icon.setImageResource(iconResId)
        binding.descriptionText.text = description.toTitleCase()
        binding.locationButton.text = getString(R.string.location, location)
        binding.temperatureText.text = temperature
        binding.windSpeedText.text = getString(R.string.wind_speed, windspeed)
        binding.humidityText.text = getString(R.string.percent, humidity)
        binding.cloudinessText.text = getString(R.string.percent, cloudiness)
    }
}

// reference: https://openweathermap.org/weather-conditions#How-to-get-icon-URL
fun getWeatherIcon(conditionId: Int) = when(conditionId) {
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
    //701, 711, 721, 731, 741, 751, 761, 762 -> R.drawable.ic_fog_large
    in 701..762 -> R.drawable.ic_fog_large
    781, 900 -> R.drawable.ic_tornado_large
    905 -> R.drawable.ic_windy_large
    906 -> R.drawable.ic_hail_large
    else -> R.drawable.ic_unknown_large
}

fun String.toTitleCase(): String {
    var output = ""
    for (word in split(" ")) {
        output += word.replaceFirstChar {
            it.uppercaseChar()
        } + " "
    }
    return output.trim()
}