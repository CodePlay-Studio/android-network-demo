package my.com.codeplay.training.network_demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import my.com.codeplay.training.network_demo.volley.VolleyManager;

import static my.com.codeplay.training.network_demo.Constants.OWM_ALL;
import static my.com.codeplay.training.network_demo.Constants.OWM_CLOUDS;
import static my.com.codeplay.training.network_demo.Constants.OWM_COUNTRY;
import static my.com.codeplay.training.network_demo.Constants.OWM_HUMIDITY;
import static my.com.codeplay.training.network_demo.Constants.OWM_ID;
import static my.com.codeplay.training.network_demo.Constants.OWM_MAIN;
import static my.com.codeplay.training.network_demo.Constants.OWM_NAME;
import static my.com.codeplay.training.network_demo.Constants.OWM_SPEED;
import static my.com.codeplay.training.network_demo.Constants.OWM_SYSTEM;
import static my.com.codeplay.training.network_demo.Constants.OWM_TEMPERATURE;
import static my.com.codeplay.training.network_demo.Constants.OWM_WEATHER;
import static my.com.codeplay.training.network_demo.Constants.OWM_WIND;
import static my.com.codeplay.training.network_demo.R.id.cloudiness;
import static my.com.codeplay.training.network_demo.R.id.humidity;

public class MainActivity extends AppCompatActivity implements NetworkCallback {
    private static final String WEATHER_BASE_URL= "http://api.openweathermap.org/data/2.5/weather?";
    private static final String PARAM_APPID		= "APPID";
    private static final String PARAM_MODE		= "mode";
    private static final String PARAM_CITY_ID	= "id";

    private static final String APP_ID = "82445b6c96b99bc3ffb78a4c0e17fca5";
    private static final String MODE = "json";
    private static final String DEFAULT_CITY_ID = "1735161"; // q=kuala lumpur

    private RelativeLayout rlNoConn, rlWeather;
    private LinearLayout llRetry, llLoading;
    private ProgressBar pbRefresh;
    private Button btnRefresh;
    private TextView tvLocation, tvTemperature, tvHumidity, tvWindSpeed, tvCloudiness;
    private ImageView ivIcon;

    private NetworkFragment mNetworkFragment;
    private String url;
    private boolean isRequesting = false;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_refresh:
            case R.id.button_retry:
                request();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlWeather = (RelativeLayout) findViewById(R.id.layout_weather);
        rlNoConn  = (RelativeLayout) findViewById(R.id.layout_no_conn);
        llRetry   = (LinearLayout) findViewById(R.id.panel_retry);
        llLoading = (LinearLayout) findViewById(R.id.panel_loading);
        pbRefresh = (ProgressBar) findViewById(R.id.progress);
        btnRefresh = (Button) findViewById(R.id.button_refresh);
        tvLocation  = (TextView) findViewById(R.id.location);
        tvTemperature = (TextView) findViewById(R.id.temperature);
        tvHumidity = (TextView) findViewById(humidity);
        tvWindSpeed = (TextView) findViewById(R.id.wind_speed);
        tvCloudiness = (TextView) findViewById(cloudiness);
        ivIcon = (ImageView) findViewById(R.id.icon);

        Uri uri = Uri.parse(WEATHER_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_APPID, APP_ID)
                .appendQueryParameter(PARAM_MODE, MODE)
                .appendQueryParameter(PARAM_CITY_ID, DEFAULT_CITY_ID)
                .build();
        mNetworkFragment = NetworkFragment.newInstance(getSupportFragmentManager(), url = uri.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        VolleyManager.getInstance(this).cancelRequest(WEATHER_BASE_URL);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public void startRequest() {
        isRequesting = true;

        if (rlNoConn.isShown()) {
            llRetry.setVisibility(View.GONE);
            llLoading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

    }

    @Override
    public void updateFromRequest(String result) {
        if (result == null) {
            rlWeather.setVisibility(View.INVISIBLE);
            rlNoConn.setVisibility(View.VISIBLE);
            llRetry.setVisibility(View.VISIBLE);
            llLoading.setVisibility(View.GONE);
        } else {
            try {
                parse(new JSONObject(result));
            } catch (JSONException e) {
                Snackbar.make(rlWeather, result, Snackbar.LENGTH_INDEFINITE)
                        .show();
            }
        }
    }

    @Override
    public void finishRequest() {
        if (rlNoConn.isShown()) {
            llRetry.setVisibility(View.VISIBLE);
            llLoading.setVisibility(View.GONE);
        }
        isRequesting = false;
    }

    private void triggerMode(boolean loading) {
        pbRefresh.setVisibility(loading? View.VISIBLE : View.GONE);
        btnRefresh.setVisibility(loading? View.INVISIBLE : View.VISIBLE);
    }

    private void request() {
        if (isRequesting)
            return;

        /* Request via headless / model fragment that encapsulates the network operation using AsyncTask.
        if (mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startRequest();
        }
        // */

        triggerMode(true);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        triggerMode(false);

                        try {
                            parse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        triggerMode(false);

                        Snackbar.make(rlWeather, error.getMessage(), Snackbar.LENGTH_INDEFINITE)
                                .show();
                    }
                }
        );

        /*
        GsonRequest<WeatherData> request = new GsonRequest<WeatherData>(url, WeatherData.class, null,
                new Response.Listener<WeatherData>() {
                    @Override
                    public void onResponse(WeatherData weatherData) {
                        triggerMode(false);

                        tvLocation.setText(getString(R.string.location,
                                weatherData.name, weatherData.sys.country));
                        tvTemperature.setText(String.valueOf(Math.round(weatherData.main.temp - 273.15)));
                        tvHumidity.setText(String.format(getResources().getString(R.string.percent),
                                weatherData.main.humidity));
                        tvWindSpeed.setText(String.format(getResources().getString(R.string.wind_speed),
                                weatherData.wind.speed));
                        tvCloudiness.setText(String.format(getResources().getString(R.string.percent),
                                weatherData.clouds.all));
                        if (weatherData.weather.size()>0) {
                            ivIcon.setImageResource(getWeatherIcon(weatherData.weather.get(0).id));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        triggerMode(false);

                        Snackbar.make(rlWeather, error.getMessage(), Snackbar.LENGTH_INDEFINITE)
                                .show();
                    }
                }
        );
        // */

        VolleyManager.getInstance(this).enqueue(request, WEATHER_BASE_URL);
    }

    private void parse(JSONObject jsonWeatherData) throws JSONException {
        tvLocation.setText(
                getString(
                        R.string.location,
                        jsonWeatherData.getString(OWM_NAME),
                        jsonWeatherData.getJSONObject(OWM_SYSTEM).getString(OWM_COUNTRY)
                )
        );

        final JSONObject mainJSON  = jsonWeatherData.getJSONObject(OWM_MAIN);
        // Temperature in Kelvin. Subtracted 273.15 from this figure to convert to Celsius.
        final double temp = mainJSON.getDouble(OWM_TEMPERATURE);
        tvTemperature.setText(String.valueOf(Math.round(temp - 273.15)));

        final int humidity = mainJSON.getInt(OWM_HUMIDITY);
        tvHumidity.setText(String.format(getResources().getString(R.string.percent), humidity));

        final double speed = jsonWeatherData.getJSONObject(OWM_WIND).getDouble(OWM_SPEED);
        final String strSpeed = String.format(getResources().getString(R.string.wind_speed), speed);
        tvWindSpeed.setText(strSpeed);

        final int cloudiness = jsonWeatherData.getJSONObject(OWM_CLOUDS).getInt(OWM_ALL);
        tvCloudiness.setText(String.format(getResources().getString(R.string.percent), cloudiness));

        final JSONArray weatherJSONArray = jsonWeatherData.getJSONArray(OWM_WEATHER);
        if (weatherJSONArray.length()>0) {
            final JSONObject jsonObject = weatherJSONArray.getJSONObject(0);
            ivIcon.setImageResource(getWeatherIcon(jsonObject.getInt(OWM_ID)));
        }
    }

    private int getWeatherIcon(int code) {
        switch (code) {
            case 200:
            case 201:
            case 202:
            case 210:
            case 211: // thunderstorm
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                return R.drawable.ic_thunderstorm_large;
            case 300:
            case 301: // drizzle
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
                return R.drawable.ic_drizzle_large;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 511:
            case 520:
            case 521:
            case 522:
            case 531:
                return R.drawable.ic_rain_large;
            case 600:
            case 601: // snow
            case 602:
            case 611:
            case 612:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:
                return R.drawable.ic_snow_large;
            case 800: // clear sky
                return R.drawable.ic_day_clear_large;
            case 801: // few clouds
                return R.drawable.ic_day_few_clouds_large;
            case 802: // scattered clouds
                return R.drawable.ic_scattered_clouds_large;
            case 803: // broken clouds
            case 804: // overcast clouds
                return R.drawable.ic_broken_clouds_large;
            case 701:
            case 711:
            case 721:
            case 731:
            case 741: // fog
            case 751:
            case 761:
            case 762:
                return R.drawable.ic_fog_large;
            case 781:
            case 900:
                return R.drawable.ic_tornado_large;
            case 905: // windy
                return R.drawable.ic_windy_large;
            case 906: // hail
                return R.drawable.ic_hail_large;

            //case 771: // squalls
            //case 901: // tropical storm
            //case 902: // hurricane
            //case 903: // cold
            //case 904: // hot
            //case 951: // calm
            //case 952: // light breeze
            //case 953: // gentle breeze
            //case 954: // moderate breeze
            //case 955: // fresh breeze
            //case 956: // strong breeze
            //case 957: // high wind, near gale
            //case 958: // gale
            //case 959: // severe gale
            //case 960: // storm
            //case 961: // violent storm
            //case 962: // hurricane
        }
        return 0;
    }
}
