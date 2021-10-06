package my.com.codeplay.training.weatherapp_v2.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitServiceManager {
    private const val baseUrl = "https://api.openweathermap.org"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl)
        .build()

    val retrofitService : WeatherService by lazy {
        retrofitBuilder.create(WeatherService::class.java)
    }
}