package com.jit.locationsource.viewModel

import androidx.lifecycle.*
import com.jit.locationsource.data.LocationDao
import com.jit.locationsource.data.LocationData
import com.jit.locationsource.data.LocationDatabase
import kotlinx.coroutines.launch

class LocationViewModel(private val locationDatabase: LocationDatabase): ViewModel() {

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

    fun deleteData(locationData: LocationData){
        viewModelScope.launch {
            locationDao?.delete(locationData)
        }
    }



}