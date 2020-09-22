package com.custom.app.ui.senseNext.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.data.model.senseNext.SNDeviceRes
import kotlinx.android.synthetic.main.item_sn_devices.view.*

class SNDeviceAdapterval(val context: Context, val devicesList: ArrayList<SNDeviceRes>,
                         val mCallback: ItemCallback) : RecyclerView.Adapter<SNDeviceAdapterval.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvColdStoreName: TextView = view.tvColdStoreName
        val tvColdStoreLocation: TextView = view.tvColdStoreLocation
        val tvColdStoreTemp: TextView = view.tvColdStoreTemp
        val ivEscalation:ImageView= view.ivEscalation
        val mainLayout: LinearLayout = view.mainLayout


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sn_devices, parent, false))
    }

    override fun getItemCount(): Int {
        return devicesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvColdStoreName.text = devicesList[position].profileName
        holder.tvColdStoreLocation.text = devicesList[position].instCenterName
        holder.tvColdStoreTemp.text = devicesList[position].temp
        when (devicesList[position].escalationLevel) {
            "0" -> {
                holder.ivEscalation.setImageResource(R.drawable.grey_down_thumb);
            }
            "1" -> {
                holder.ivEscalation.setImageResource(R.drawable.level1);
            }
            "2" -> {
                holder.ivEscalation.setImageResource(R.drawable.level2);
            }
            "3" -> {
                holder.ivEscalation.setImageResource(R.drawable.level3);
            }
            else->{
                holder.ivEscalation.setImageResource(R.drawable.like_green);
            }
        }

        holder.mainLayout.setOnClickListener {
            mCallback.clickItem(position) }

    }
}