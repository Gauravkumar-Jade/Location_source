package com.jit.locationsource.ui

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.SphericalUtil
import com.jit.locationsource.LocationApplication
import com.jit.locationsource.R
import com.jit.locationsource.data.LocationData
import com.jit.locationsource.databinding.ActivityPlaceBinding
import com.jit.locationsource.viewModel.LocationViewModelFactory
import com.jit.locationsource.viewModel.PlacesViewModel
import java.util.*
import javax.inject.Inject

class PlaceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPlaceBinding
    private lateinit var viewModel: PlacesViewModel


    @Inject
    lateinit var viewModelFactory: LocationViewModelFactory


    private var name: String? =null
    private var addresss: String? = null
    private var lat: String? = null
    private var long: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as LocationApplication).locationComponent.injectPlaceActivity(this)

        viewModel = ViewModelProvider(this,viewModelFactory).get(PlacesViewModel::class.java)


        setUpGoogleMap()
        setUpPlaceApi()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    private fun setUpPlaceApi() {

        // Fetching API_KEY which we wrapped
        val apiKey = getAPIKey()

        if (!Places.isInitialized()){
            Places.initialize(applicationContext, apiKey)
        }

        val placeSearchFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        placeSearchFragment.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
            )
        )

        placeSearchFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onError(status: Status) {
                Toast.makeText(applicationContext,status.statusMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {

                if(::mMap.isInitialized){

                    val latLng = place.latLng

                    val geocoder = Geocoder(this@PlaceActivity, Locale.getDefault())
                    val address:List<Address> =
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude,1)!!

                    name = address.get(0).locality

                    if(name?.length == null){
                        name = place.name
                    }

                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLng!!)
                    markerOptions.title(name)

                    val locationMarker = mMap.addMarker(markerOptions)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f))
                    locationMarker?.showInfoWindow()

                    addresss = place.address!!
                    lat = place.latLng!!.latitude.toString()
                    long = place.latLng!!.longitude.toString()


                }
                else{
                    Toast.makeText(applicationContext, "Map Not Ready", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.btSavePlace.setOnClickListener {

           // val size = intent?.getStringExtra("size")?.toInt()

            viewModel.allData.observe(this){
                val size = it.size
                if (size == 0){
                    val locationData = LocationData(
                        place_name = name!!,
                        place_address = addresss!!,
                        place_lat = lat!!,
                        place_long = long!!,
                        place_remark = "Primary",
                        place_dist = null
                    )
                    viewModel.addData(locationData)
                    startActivity(Intent(this@PlaceActivity, MainActivity::class.java))
                    finish()
                }else {
                    val mLocationId = intent.getStringExtra("id")
                    Log.i("LOCATION_ID", mLocationId.toString())
                    addOrUpdate(mLocationId, null)
                }

            }

        }
    }

    private fun addOrUpdate(mLocationId: String?, remark: String?) {
        if (mLocationId != null) {
            if (name?.length == 0 && addresss?.length == 0) {
                Toast.makeText(applicationContext, "Please Select Place", Toast.LENGTH_SHORT).show()
            } else {

                viewModel.allData.observe(this){
                    val locationData = LocationData(
                        mLocationId.toInt(),
                        place_name = name!!,
                        place_address = addresss!!,
                        place_lat = lat!!,
                        place_long = long!!,
                        place_remark = null,
                        place_dist = calculateDistance(it,lat?.toDouble()!!,long?.toDouble()!!).toString()
                    )
                    viewModel.updateData(locationData)
                    startActivity(Intent(this@PlaceActivity, MainActivity::class.java))
                    finish()
                }
            }

        } else {
            if (name?.length == 0 && addresss?.length == 0) {
                Toast.makeText(applicationContext, "Please Select Place", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.allData.observe(this){
                    val locationData = LocationData(
                        place_name = name!!,
                        place_address = addresss!!,
                        place_lat = lat!!,
                        place_long = long!!,
                        place_remark = null,
                        place_dist = calculateDistance(it,lat?.toDouble()!!,long?.toDouble()!!).toString()
                    )
                    viewModel.addData(locationData)
                    startActivity(Intent(this@PlaceActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun getAPIKey(): String {
        val key: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = key.metaData["com.google.android.geo.API_KEY"]
        return value.toString()
    }

    // Setup Google Map
    private fun setUpGoogleMap() {
        val mapFragment = supportFragmentManager.
        findFragmentById(R.id.frag_map_container) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    // Callback for onMapReady
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    private fun calculateDistance(itemList: List<LocationData>?, place_lat:Double, place_long:Double):Long {

        val first_lat = itemList?.get(0)?.place_lat?.toDouble()
        val first_long = itemList?.get(0)?.place_long?.toDouble()

        val firstPlace = LatLng(first_lat!!,first_long!!)


        val otherPlace = LatLng(place_lat,place_long)

        val distance = SphericalUtil.computeDistanceBetween(firstPlace,otherPlace)

        return (distance/1000).toLong()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        this@PlaceActivity.finish()
        super.onBackPressed()
    }


}