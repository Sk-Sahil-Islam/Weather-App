package com.example.weathercompose.util

import com.example.weathercompose.data.remote.responses.Hour

fun flattenHoursList (listOfHours: List<List<Hour>>): List<Hour> {
    return listOfHours.flatten()
}

