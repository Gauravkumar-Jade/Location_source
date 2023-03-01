package com.jit.locationsource.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "locationData")
data class LocationData(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var place_name: String?,
    var place_address: String?,
    var place_lat: String?,
    var place_long: String?,
    var place_remark: String?,
    var place_dist:String?)
