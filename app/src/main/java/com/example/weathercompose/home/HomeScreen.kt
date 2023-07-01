package com.example.weathercompose.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.weathercompose.util.Constants.API_KEY
import com.example.weathercompose.util.Resource
import com.example.weathercompose.R
import com.example.weathercompose.data.remote.responses.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()
    Surface(
        color = Color(0xFF101010),
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            TopSection(city = "Kolkata")
            Spacer(modifier = Modifier.size(250.dp))
            BottomSection(apiKey = API_KEY, city = "Kolkata")
        }
    }
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    city: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city,
                color = Color.White,
                fontSize = 24.sp
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_location_city_24),
                contentDescription = "City location.",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomSection(
    viewModel: WeatherHomeViewModel = hiltViewModel(),
    apiKey: String,
    city: String
) {
    val weatherInfo = produceState<Resource<Weather>>(initialValue = Resource.Loading()) {
        value = viewModel.getWeatherInfo(apiKey = apiKey, city = city)
    }.value
    when (weatherInfo) {
        is Resource.Error -> {
            Text(
                text = weatherInfo.message!!,
                color = Color.Red
            )
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = Color.Blue
            )
        }
        is Resource.Success -> {
            WeatherSection(weatherInfo)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherSection(
    weatherInfo: Resource<Weather>
) {
    val date = remember {
        LocalDateTime.now()
    }
    val currentTimeIterator = date.hour + 1
    val listOfHours: MutableList<Hour> = mutableListOf()
    for (forecast in weatherInfo.data!!.forecast.forecastday) {
        for (hour in forecast.hour)
            listOfHours.add(hour)
    }
    Box {
        Column {
            CurrentTemperatureAndDate(
                current = weatherInfo.data.current,
                forecast = weatherInfo.data.forecast,
                date = date
            )
            Spacer(modifier = Modifier.size(16.dp))
            HourlyForecast(
                hours = listOfHours,
                currentTimeIterator = currentTimeIterator
            )
            Spacer(modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 16.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.DarkGray))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentTemperatureAndDate(
    current: Current,
    forecast: Forecast,
    date: LocalDateTime
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val style = TextStyle(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box {
                Text(
                    text = current.temp_c.toInt().toString(),
                    fontSize = 100.sp,
                    color = Color.White,
                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    val superscript = SpanStyle(
                        baselineShift = BaselineShift.Superscript,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(superscript) {
                                append("o")
                            }
                            append("C")
                        },
                        style = style
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = current.condition.text,
                        style = style
                    )
                }
            }
        }
        Row {
            val style2 = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )

            val formatter = DateTimeFormatter.ofPattern("MMM d E")
            val formatted = date.format(formatter)

            Text(
                text = formatted,
                style = style2,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = (forecast.forecastday[0].day.maxtemp_c.toInt()).toString() +
                        "/" +
                        (forecast.forecastday[0].day.mintemp_c.toInt()).toString(),
                style = style2
            )
        }
    }
}

@Composable
fun HourlyForecast(
    hours: List<Hour>,
    currentTimeIterator: Int
) {
    Box {
        LazyRow (horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(hours.count() - currentTimeIterator) {
                IndividualHour(hour = hours[currentTimeIterator + it])
            }
        }
    }
}

@Composable
fun IndividualHour(
    hour: Hour
) {
    Box {
        val style = TextStyle(
            color = Color.White,
            fontSize = 17.sp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = hour.time.drop(11),
                style = style
            )
            val imageUrl = "https:" + hour.condition.icon
            val superscript = SpanStyle(
                baselineShift = BaselineShift.Superscript,
                color = Color.White,
                fontSize = 10.sp
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .align(CenterHorizontally)
            )
            Row {
                Text(
                    text = hour.temp_c.toInt().toString(),
                    style = style
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(superscript) {
                            append("o")
                        }
                        append("C")
                    },
                    style = style
                )
            }
        }
    }
}