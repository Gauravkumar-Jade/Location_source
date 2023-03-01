package com.jit.locationsource.di

import android.content.Context
import androidx.room.Room
import com.jit.locationsource.data.LocationDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class LocationModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): LocationDatabase{
        return Room.databaseBuilder(
            context,
            LocationDatabase::class.java,
            "location_db")
            .build()
    }
}