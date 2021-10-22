package com.p2m.whetherappcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.p2m.whetherappcompose.ui.theme.WhetherAppComposeTheme
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.coroutineContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhetherAppComposeTheme {
                val modifier = Modifier
                Navigation(modifier = modifier)
            }
        }
    }
}

@Composable
fun Navigation(modifier: Modifier){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.EntranceScreen.route){

        composable(Screen.EntranceScreen.route){
            EntranceScreen(modifier = modifier, navController = navController)
        }

        composable(
            route = Screen.DataScreen.route+"/{city}/{country}",
            arguments = listOf(navArgument("city"){type= NavType.StringType
                                                  defaultValue= "vadodara"
                                                  nullable = true},
                                navArgument("country"){
                                                 type= NavType.StringType
                                                  defaultValue= "india"
                                                 nullable = true}
        )){
            it.
            arguments?.
            getString("city")?.
            let { it1 -> DataScreen(modifier = modifier , city = it1, country = it.arguments?.getString("country")!!) }
        }

    }

}

@Composable
fun EntranceScreen(modifier: Modifier, navController: NavController){

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(Color.Cyan, Color.DarkGray)),
                RectangleShape, alpha = 0.5f
            )
    ) {
        Column(
            modifier =
            modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Cyan,
                            Color.DarkGray
                        )
                    ), RectangleShape, alpha = 0.5f
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = "SWeather(Compose)",
                modifier = modifier.padding(10.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                color= Color.Black
            )

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                var cityName by remember {mutableStateOf("")}
                var countryName by remember{ mutableStateOf("india") }

                OutlinedTextField(
                    value = cityName,
                    onValueChange = {cityName = it},
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, bottom = 15.dp),
                    label = { Text(text = "City")}
                )

                OutlinedTextField(
                    value = countryName,
                    onValueChange = {countryName= it},
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, bottom = 30.dp),
                    label = { Text(text = "Country")}
                )

                Button(onClick = { navController.navigate(Screen.DataScreen.route.plus("/$cityName/$countryName")) }, modifier = modifier
                    .width(170.dp)
                    .height(50.dp)) {
                    Text(text = "Show Data")
                }
            }

        }
    }

}

@Composable
fun DataScreen(modifier: Modifier, city :String, country:String){
    val apiKey = "566e7e0c3208750b42b82c39d93af343"
    val url = "https://api.openweathermap.org/data/2.5/weather?q=$city,$country&appid=566e7e0c3208750b42b82c39d93af343"


    Log.d("Pinkesh", "DataScreen: city::  $city and country:: $country")

    val cityCountryRow :String = "$city,$country"
    var lastUpdate :String by remember{mutableStateOf("")}
    var cloudState :String by remember{mutableStateOf("")}
    var temperature :String by remember{mutableStateOf("")}
    var minTemp :String by remember{mutableStateOf("")}
    var maxTemp :String by remember{mutableStateOf("")}
    var humidity :String by remember{mutableStateOf("")}
    var visibility :String by remember{mutableStateOf("")}
    var sunrise :String by remember{mutableStateOf("")}
    var sunset :String by remember{mutableStateOf("")}
    var pressure :String by remember{mutableStateOf("")}
    var speed :String by remember{mutableStateOf("")}
    var degree :String by remember{mutableStateOf("")}
    var live by remember {
        mutableStateOf(false)
    }
    val TAG = "Pinkesh"

    fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy h:mm a")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    val jsonObjectRequest:JsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,null,
        { response ->
            val OBJ = response.getJSONObject("main")
            Log.d(TAG, "DataScreen: ")
            temperature = OBJ.getString("temp")
            minTemp = OBJ.getString("temp_min")
            maxTemp = OBJ.getString("temp_max")
            pressure = OBJ.getString("pressure")
            humidity = OBJ.getString("humidity")
            live = true

            Log.d(TAG, "DataScreen: This is main obj::: $OBJ")
            val t = temperature.toFloat() -  273.15
            val j = t.toString().substring(0,5)
            val z = "$j°C"
            temperature = z

            val va = minTemp.toFloat() - 273.15
            val vp = "$va"
            val vz = vp.substring(0,5)+"°C"
            minTemp = vz

            val jk = maxTemp.toFloat() - 273.15
            val jd = "$jk"
            val js = jd.substring(0,5)+"°C"
            maxTemp = js

            val windOBJ = response.getJSONObject("wind")
            val se = windOBJ.getString("speed")
            val de = windOBJ.getString("deg")
            speed = "${se}meter/sec"
            degree = "${de}°"


            val weatherarray = response.getJSONArray("weather")

            var dscn = ""
            for (i in 0 until weatherarray.length()) {
                dscn = weatherarray.getString(i)
            }

            val descriptionstart: Int = dscn.indexOf("description")
            val descriptionend: Int = dscn.indexOf("icon")
            val dscription: String = dscn.substring(descriptionstart, descriptionend)
            val dscnfinal = dscription.substring(0, dscription.length - 2)
            val jjjj = dscnfinal.substring(14, dscnfinal.length - 1)
            cloudState = jjjj

            val sysOBJ = response.getJSONObject("sys")
            sunrise = sysOBJ.getString("sunrise")
            sunset = sysOBJ.getString("sunset")

            visibility = response.getString("visibility")
            val jj:String = response.getString("dt")

            lastUpdate = "Last updated at ${getDateTime(jj)}"


            Log.d("Pinkesh", "DataScreen: $response")
        },
        { error ->
            Log.d("Pinkesh", "DataScreen: This is error message::: $error")
            //TODO: Handle error
        }
    )
    val context = LocalContext.current
    Volley.newRequestQueue(context).add(jsonObjectRequest)

if (live){

    Log.d("Pinkesh", "DataScreen: $humidity")
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(Color.Cyan, Color.DarkGray)),
                    RectangleShape, alpha = 0.5f
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = cityCountryRow,
                    modifier = modifier.
                    padding(top = 20.dp, bottom = 5.dp),
                    fontSize = 25.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = lastUpdate,
                    modifier = modifier.padding(bottom = 30.dp),
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )

                Text(
                    text = cloudState,
                    color = Color.DarkGray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = temperature,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 35.sp,
                    modifier = modifier.padding(10.dp),
                    color = Color.DarkGray
                )

                Text(
                    text = "Minimum temp. $minTemp",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal)

                Text(
                    text = "Maximum temp. $maxTemp",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal,
                    modifier = modifier.padding(bottom = 20.dp)
                )

                Text(
                    text = "Humidity: ${humidity}%",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(6.dp)
                )
                Text(
                    text = "Visibility: $visibility meter",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

            }

            Column(modifier = modifier
                .fillMaxSize()
                .weight(1f)
                .padding(bottom = 30.dp), verticalArrangement = Arrangement.Bottom) {

                Row(modifier= modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    getDateTime(sunrise)?.let {
                        BoxShape(
                            modifier = modifier,
                            int = R.drawable.sunrise,
                            title = "Sunrise",
                            data = it
                        )
                    }

                    getDateTime(sunset)?.let {
                        BoxShape(
                            modifier = modifier,
                            int = R.drawable.sunset,
                            title = "Sunset",
                            data = it
                        )
                    }

                    BoxShape(
                        modifier = modifier,
                        int = R.drawable.wind,
                        title = "Wind",
                        data = speed
                    )
                }

                Row(modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    BoxShape(
                        modifier = modifier,
                        int = R.drawable.pressure,
                        title = "Pressure",
                        data = "$pressure hPa"
                    )

                    BoxShape(
                        modifier = modifier,
                        int = R.drawable.humidity,
                        title = "Humidity",
                        data = "$humidity %"
                    )

                    BoxShape(
                        modifier = modifier,
                        int = R.drawable.info,
                        title = "Created by",
                        data = "P2M"
                    )

                }

            }

        }

    }
}
}

@Composable
fun BoxShape(modifier: Modifier,int: Int, title:String, data:String){

    Card(modifier = modifier
        .size(110.dp, 120.dp)
        .padding(5.dp),
        elevation = 5.dp
    ) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Cyan)
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(id = int),
                contentDescription = title,
                tint = Color.Black
            )
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = modifier.padding(bottom = 1.5.dp)
            )

            Text(

                text = data,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )



        }

    }


}

/*                                           Written by Pinkesh P. Machhi (P2M) with love of Coding.
                                                                 HAPPY CODING                                                                                                     */

























