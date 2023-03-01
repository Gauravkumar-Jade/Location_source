package com.jit.locationsource.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.jit.locationsource.data.LocationData
import com.jit.locationsource.databinding.CustomPlaceListBinding
import javax.inject.Inject

class LocationAdapter @Inject constructor():RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    private var mLocationData: List<LocationData>? = null
    private var mRecyclerViewListener: RecyclerViewListener? =null

    fun bindData(locationData: List<LocationData>) {
        this.mLocationData = locationData
        notifyDataSetChanged()
    }

    fun bindAdapter(recyclerViewListener: RecyclerViewListener){
        this.mRecyclerViewListener = recyclerViewListener
    }

    inner class LocationViewHolder(val binding: CustomPlaceListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = CustomPlaceListBinding.inflate(inflater, parent, false)

        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {

        val data = mLocationData?.get(holder.adapterPosition)


        if (data?.place_remark == "Primary") {
            holder.binding.apply {
                tvName.text = data!!.place_name
                tvAddress.text = data.place_address
                tvPrimary.visibility = View.VISIBLE
                tvDistance.visibility = View.INVISIBLE
                ivDelete.setOnClickListener {
                    mRecyclerViewListener?.deleteItem(data)
                }

                ivEdit.setOnClickListener {
                    mRecyclerViewListener?.editItem(data)
                }
            }
        } else {
            holder.binding.apply {
                tvName.text = data!!.place_name
                tvAddress.text = data.place_address
                tvPrimary.visibility = View.INVISIBLE
                tvDistance.visibility = View.VISIBLE

                val distance = "Distance: ${data.place_dist} Km"

                tvDistance.text = distance

                ivDelete.setOnClickListener {
                    mRecyclerViewListener?.deleteItem(data)
                }
                ivEdit.setOnClickListener {
                    mRecyclerViewListener?.editItem(data)
                }
            }
        }
    }

        override fun getItemCount(): Int {
            return mLocationData?.size!!
        }

}

interface RecyclerViewListener{
    fun deleteItem(locationData: LocationData)
    fun editItem(locationData: LocationData)
}


