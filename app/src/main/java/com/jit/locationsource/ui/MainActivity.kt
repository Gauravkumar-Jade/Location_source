package com.jit.locationsource.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jit.locationsource.LocationApplication
import com.jit.locationsource.MapsActivity
import com.jit.locationsource.R
import com.jit.locationsource.adapter.LocationAdapter
import com.jit.locationsource.adapter.RecyclerViewListener
import com.jit.locationsource.data.LocationData
import com.jit.locationsource.databinding.ActivityMainBinding
import com.jit.locationsource.viewModel.LocationViewModel
import com.jit.locationsource.viewModel.LocationViewModelFactory
import java.lang.System.exit
import javax.inject.Inject

class MainActivity : AppCompatActivity(), RecyclerViewListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LocationViewModel

    @Inject
    lateinit var adapter: LocationAdapter

    @Inject
    lateinit var viewModelFactory: LocationViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as LocationApplication).locationComponent.injectMainActivity(this)

        viewModel = ViewModelProvider(this,viewModelFactory).get(LocationViewModel::class.java)
        adapter.bindAdapter(this)

        setup()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }


    private fun setup() {
        binding.btAddPlace.setOnClickListener {
            viewModel.allData.observe(this){
                startActivity(Intent(this@MainActivity, PlaceActivity::class.java)
                    .putExtra("size",it.size.toString()))
            }
        }

      binding.fabButton.setOnClickListener {
            viewModel.allData.observe(this){
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
        }

        viewModel.allData.observe(this){
            adapter.bindData(it)

            Log.i("LOCATION_",it.toString())
            binding.rvPlaceList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
            binding.rvPlaceList.adapter = adapter
        }


    }

    override fun deleteItem(locationData: LocationData) {
        viewModel.deleteData(locationData)
        viewModel.getData()
    }

    override fun editItem(locationData: LocationData) {
        startActivity(Intent(this@MainActivity, PlaceActivity::class.java)
            .putExtra("id",locationData.id.toString()))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){

            R.id.down -> {
                onAscending()
                return true
            }

            R.id.upward ->  {
                onDescending()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun onAscending() {
        viewModel.allData.observe(this){ it ->
            val location = it?.sortedBy { it.place_dist }
            adapter.bindData(location!!)
        }
    }

    private fun onDescending() {
        viewModel.allData.observe(this){ it ->
            val location = it?.sortedByDescending { it.place_dist }
            adapter.bindData(location!!)
        }
    }
}