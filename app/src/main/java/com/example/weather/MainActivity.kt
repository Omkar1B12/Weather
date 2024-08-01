package com.example.weather

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//01b16b784d3f4f5340ab6c8b840da308
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchWeatherData("london")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView2
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response=retrofit.getWeatherData(cityName,"01b16b784d3f4f5340ab6c8b840da308","metric")
        response.enqueue(object :Callback<Weathera>{
            override fun onResponse(call: Call<Weathera>, response: Response<Weathera>) {
                val responseBody=response.body()
                if (response.isSuccessful&&responseBody!=null){
                    val temperature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windSpeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"Unknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min


                binding.temp.text="$temperature ℃"
                binding.weather.text=condition
                    binding.maxTemp.text="Max Temp:$maxTemp ℃"
                    binding.minTemp.text="Min Temp: $minTemp ℃"
                    binding.humidity.text="$humidity %"
                    binding.windspeed.text="$windSpeed m/s"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.sunny.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityName"
                //   Log.d("TAG","onResponse:$temperature")

                    changeBackgroundAccordingToWeather(condition)
                }
            }

            override fun onFailure(call: Call<Weathera>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })

    }

    private fun changeBackgroundAccordingToWeather(conditions:String) {
    when (conditions){
        "Clear Sky","Sunny","Clear"->{
            binding.root.setBackgroundResource(R.drawable.sunny)
            binding.lottieAnimationView.setAnimation(R.raw.sun)
        }
        "Partly Clouds","Clouds","Overcast","Mist","Foggy","Haze"->{
            binding.root.setBackgroundResource(R.drawable.cloudy)
            binding.lottieAnimationView.setAnimation(R.raw.cloud)
        }
        "Rain","Light Rain","Drizzle","Moderate Rain","Showers","heavy Rain"->{
            binding.root.setBackgroundResource(R.drawable.raiiin)
            binding.lottieAnimationView.setAnimation(R.raw.raining)
        }
        "Light Snow","Moderate snow","Heavy Snow","Blizzard"->{
            binding.root.setBackgroundResource(R.drawable.snowfall)
            binding.lottieAnimationView.setAnimation(R.raw.snow)
        }
        else ->{
            binding.root.setBackgroundResource(R.drawable.sunny)
            binding.lottieAnimationView.setAnimation(R.raw.sun)
        }
    }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date():String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            return sdf.format((Date()))
    }

    private fun time(timestamp: Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}