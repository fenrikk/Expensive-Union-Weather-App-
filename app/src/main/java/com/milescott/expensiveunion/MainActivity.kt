package com.milescott.expensiveunion

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.milescott.expensiveunion.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var api: WeatherApi

    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherbit.io")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        api = retrofit.create(WeatherApi::class.java)

        binding.button.setOnClickListener {
            compositeDisposable.add(
                api.getWeather()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        binding.windDirection.text = "Wind direction: ${it.data[0].wind_cdir_full}"
                        binding.windSpeed.text = "Wind speed: ${it.data[0].wind_spd}"
                        binding.sunset.text = "Sunset: ${it.data[0].sunset}"
                        binding.snow.text = "Drops: ${it.data[0].snow}"
                        binding.uv.text = "UV index: ${it.data[0].uv}"
                        binding.sunrise.text = "Sunrise: ${it.data[0].sunrise}"
                        binding.description.text =
                            "Weather description: ${it.data[0].weather.description}"
                        binding.temp.text = "Temperature: ${it.data[0].temp}"
                    }, {
                        Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
                    })
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}