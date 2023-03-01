package com.jit.locationsource.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jit.locationsource.data.LocationDatabase
import javax.inject.Inject

class LocationViewModelFactory @Inject constructor(private val locationDatabase: LocationDatabase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LocationViewModel::class.java)){
            return LocationViewModel(locationDatabase) as T
        }
        if(modelClass.isAssignableFrom(PlacesViewModel::class.java)){
            return PlacesViewModel(locationDatabase) as T
        }
        if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            return MapViewModel(locationDatabase) as T
        }

        throw IllegalArgumentException("No ViewModel")
    }
}