package com.jit.locationsource.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jit.locationsource.data.LocationDao
import com.jit.locationsource.data.LocationData
import com.jit.locationsource.data.LocationDatabase
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlacesViewModel @Inject constructor(private val locationDatabase: LocationDatabase): ViewModel() {

    private var locationDao: LocationDao? =null

    private var _allData = MutableLiveData<List<LocationData>>()
    val allData: LiveData<List<LocationData>> get() = _allData

    init {
        locationDao = locationDatabase.locationDao()
    }

    fun getData(){
        viewModelScope.launch {
            _allData.postValue(locationDao?.getData())
        }
    }

    fun addData(locationData: LocationData){
        viewModelScope.launch {
            locationDao?.insertData(locationData)
        }
    }

    fun updateData(locationData: LocationData){
        viewModelScope.launch {
            locationDao?.update(locationData)
        }
    }
}