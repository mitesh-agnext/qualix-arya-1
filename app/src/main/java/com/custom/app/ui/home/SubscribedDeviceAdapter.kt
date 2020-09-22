package com.custom.app.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import timber.log.Timber

class SubscribedDeviceAdapter(val context: Context, val subscribeddetails: ArrayList<DeviceItem>) : RecyclerView.Adapter<SubscribedDeviceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var holder: ViewHolder? = null
        holder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_spinner_plain, parent, false))
        return holder
    }

    override fun getItemCount(): Int {
        return subscribeddetails.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            holder.tvTitleName!!.text = "${subscribeddetails[position].device_name}"
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvTitleName: TextView? = null
    }

}