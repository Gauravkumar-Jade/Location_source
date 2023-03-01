package com.jit.locationsource.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert
    suspend fun insertData(locationData: LocationData):Long

    @Query("SELECT * FROM locationData")
    suspend fun getData(): List<LocationData>

    @Query("DELETE FROM locationData")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(locationData: LocationData)

    @Update
    suspend fun update(locationData: LocationData)
}