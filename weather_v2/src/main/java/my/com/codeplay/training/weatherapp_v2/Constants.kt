package my.com.codeplay.training.weatherapp_v2

const val INTERVAL_IN_MILLIS = 5 * 60 * 1000L

const val KEY_CITY_ID = "city_id"
const val KEY_WEATHER_ICON = "weather_icon"
const val KEY_WEATHER_DESC = "weather_desc"
const val KEY_LOCATION = "location"
const val KEY_TEMPERATURE = "temperature"
const val KEY_WIND_SPEED = "wind_speed"
const val KEY_HUMIDITY = "humidity"
const val KEY_CLOUDINESS = "cloudiness"
const val KEY_LONGITUDE = "longitude"
const val KEY_LATITUDE = "latitude"
const val KEY_TIMESTAMP = "timestamp"

const val EXTRA_CITY_ID = "${BuildConfig.APPLICATION_ID}.extra.CITY_ID"
const val EXTRA_CITY_NAME = "${BuildConfig.APPLICATION_ID}.extra.CITY_NAME"
const val EXTRA_CITY_LON = "${BuildConfig.APPLICATION_ID}.extra.CITY_LON"
const val EXTRA_CITY_LAT = "${BuildConfig.APPLICATION_ID}.extra.CITY_LAT"