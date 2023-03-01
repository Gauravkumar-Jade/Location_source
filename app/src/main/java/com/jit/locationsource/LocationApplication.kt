package com.jit.locationsource

import android.app.Application
import com.jit.locationsource.di.DaggerLocationComponent
import com.jit.locationsource.di.LocationComponent

class LocationApplication:Application() {

    lateinit var locationComponent: LocationComponent

    override fun onCreate() {
        super.onCreate()
        locationComponent = DaggerLocationComponent.factory().create(this)
    }
}