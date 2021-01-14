package com.affirm.takehome.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.domain.YelpController
import com.affirm.takehome.domain.ZomatoController
import kotlin.properties.Delegates

enum class RestaurantEnum {
    YELP,
    ZOMATO
}

private const val TAG = "RestaurantViewModel"

class RestaurantViewModel : ViewModel() {

    private val yelpController by lazy {
        YelpController()
    }

    private val zomatoController by lazy {
        ZomatoController()
    }

    private val restaurants: MutableLiveData<List<Restaurant>> by lazy {
        MutableLiveData<List<Restaurant>>()
    }

    private var currentLocation: Location? = null

    private var currentRestaurant = RestaurantEnum.YELP

    var index: Int by Delegates.observable(0) { _, _, newValue ->
        if (newValue == restaurants.value?.size) {
            loadRestaurants()
        }
    }

    private val yelpObserver = Observer<List<Restaurant>> { yelpRestaurants ->
        val newRestaurants = mutableListOf<Restaurant>()
        restaurants.value?.let { restaurantList ->
            newRestaurants.addAll(restaurantList)
        }
        yelpRestaurants?.let { restaurantList ->
            newRestaurants.addAll(restaurantList)
        }
        restaurants.value = newRestaurants
    }

    private val zomatoObserver = Observer<List<Restaurant>> { zomatoRestaurants ->
        val newRestaurants = mutableListOf<Restaurant>()
        restaurants.value?.let { restaurantList ->
            newRestaurants.addAll(restaurantList)
        }
        zomatoRestaurants?.let { restaurantList ->
            newRestaurants.addAll(restaurantList)
        }
        restaurants.value = newRestaurants
    }

    init {
        yelpController.getRestaurants().observeForever(yelpObserver)
        zomatoController.getRestaurants().observeForever(zomatoObserver)
    }

    fun setLocation(location: Location) {
        currentLocation = location
    }


    fun getRestaurants(): LiveData<List<Restaurant>> {
        return restaurants
    }

    fun loadRestaurants() {
        currentLocation?.let { location ->
            when(currentRestaurant) {
                RestaurantEnum.YELP -> {
                    yelpController.loadRestaurants(location)
                    currentRestaurant = RestaurantEnum.ZOMATO
                }
                RestaurantEnum.ZOMATO -> {
                    zomatoController.loadRestaurants(location)
                    currentRestaurant = RestaurantEnum.YELP
                }
            }
        }
    }

    override fun onCleared() {
        yelpController.getRestaurants().removeObserver(yelpObserver)
        zomatoController.getRestaurants().removeObserver(zomatoObserver)
        super.onCleared()
    }

}