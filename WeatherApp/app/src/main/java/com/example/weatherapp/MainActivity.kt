package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var zipCodeLayout: LinearLayout
    lateinit var retryLayout: LinearLayout
    lateinit var weather1: LinearLayout
    lateinit var weather2: LinearLayout
    lateinit var weather3: LinearLayout
    lateinit var zipCode: EditText
    lateinit var retryBtn: Button
    lateinit var submittBtn: Button
    lateinit var country: TextView
    lateinit var date: TextView
    lateinit var temprature: TextView
    lateinit var description: TextView
    lateinit var sunrise: TextView
    lateinit var sunset: TextView
    lateinit var pressure: TextView
    lateinit var humidity: TextView
    lateinit var wind: TextView
    lateinit var refresh: TextView
    lateinit var tempLow: TextView
    lateinit var tempHigh: TextView
    private var city = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        zipCodeLayout = findViewById(R.id.zipCodeLayout)
        retryLayout = findViewById(R.id.retryLayout)
        weather1 = findViewById(R.id.weather1Layout)
        weather2 = findViewById(R.id.weather2Layout)
        weather3 = findViewById(R.id.weather3Layout)
        sunrise = findViewById(R.id.sunrise)
        sunset = findViewById(R.id.sunset)
        wind = findViewById(R.id.wind)
        pressure = findViewById(R.id.pressure)
        humidity = findViewById(R.id.humidity)
        refresh = findViewById(R.id.refresh)
        zipCode = findViewById(R.id.zipCode)
        submittBtn = findViewById(R.id.submitBtn)
        retryBtn = findViewById(R.id.retryBtn)
        country = findViewById(R.id.country)
        date = findViewById(R.id.date)
        temprature = findViewById(R.id.temp)
        description = findViewById(R.id.description)
        tempLow = findViewById(R.id.tempLow)
        tempHigh = findViewById(R.id.tempHigh)

        submittBtn.setOnClickListener {
            if (zipCode.text.isEmpty()) {
                    zipCodeLayout.isVisible = false
                    retryLayout.isVisible = true
                }else {
                zipCodeLayout.isVisible = false
                retryLayout.isVisible = false
                weather1.isVisible = true
                weather2.isVisible = true
                weather3.isVisible = true
                city = zipCode.text.toString();
                requestApi()

            }

            }
        retryBtn.setOnClickListener {
            retryLayout.isVisible= false
            zipCodeLayout.isVisible= true
        }
    }
    private fun requestApi() {

        CoroutineScope(Dispatchers.IO).launch {

            val data = async {

                fetchRandomAdvice()

            }.await()

            if (data.isNotEmpty())
            {

                updateWeather(data)
            }

        }

    }

    private fun fetchRandomAdvice():String{

        var response=""
        try {
            response =
                URL("https://api.openweathermap.org/data/2.5/weather?zip=$city&units=metric&appid=64f8889eeae2eb0cb32cffb2bc486613").readText(Charsets.UTF_8)

        }catch (e:Exception)
        {
            println("Error $e")

        }
        return response

    }
    private suspend fun updateWeather(data:String){
        withContext(Dispatchers.Main)
        {

            val jsonObj = JSONObject(data)
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val windS = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val currentTemperature = main.getString("temp")
            val temp = try{
                currentTemperature.substring(0, currentTemperature.indexOf(".")) + "°C"
            }catch(e: Exception){
                currentTemperature + "°C"
            }
            temprature.text = temp
            val minTemperature = main.getString("temp_min")
            val tempMin = "Low: " + minTemperature.substring(0, minTemperature.indexOf("."))+"°C"
            tempLow.text =tempMin
            val maxTemperature = main.getString("temp_max")
            val tempMax = "High: " + maxTemperature.substring(0, maxTemperature.indexOf("."))+"°C"
            tempHigh.text = tempMax
            val pressureW = main.getString("pressure")
            pressure.text = pressureW
            val humidityW = main.getString("humidity")
            humidity.text = humidityW
            val sunriseW:Long = sys.getLong("sunrise")
            sunrise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunriseW*1000))

            val sunsetW:Long = sys.getLong("sunset")
            sunset.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunsetW*1000))

            val windSpeed = windS.getString("speed")
            wind.text= windSpeed
            val weatherDescription = weather.getString("description")
            description.text = weatherDescription
            val address = jsonObj.getString("name")+", "+sys.getString("country")
                country.text = address
            val lastUpdate:Long = jsonObj.getLong("dt")
            val lastUpdateText = "Updated at: " + SimpleDateFormat(
                "dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(lastUpdate*1000))
            date.text = lastUpdateText

        }
    }
}