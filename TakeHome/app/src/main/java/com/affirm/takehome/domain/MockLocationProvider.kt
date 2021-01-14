package com.affirm.takehome.domain

import android.location.Location

class LocationProvider {

    fun currentLocation() {
        val location = Location("MockLocation")
        location.longitude = -73.9857
        location.latitude = 40.7484
        location
    }

}