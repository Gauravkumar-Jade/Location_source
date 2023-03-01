package com.jit.locationsource.di

import android.content.Context
import com.jit.locationsource.MapsActivity
import com.jit.locationsource.ui.MainActivity
import com.jit.locationsource.ui.PlaceActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LocationModule::class])
interface LocationComponent {

    fun injectMainActivity(activity: MainActivity)

    fun injectPlaceActivity(activity: PlaceActivity)

    fun injectMapActivity(activity: MapsActivity)


    @Component.Factory
    interface factory{
        fun create(@BindsInstance context: Context): LocationComponent
    }
}