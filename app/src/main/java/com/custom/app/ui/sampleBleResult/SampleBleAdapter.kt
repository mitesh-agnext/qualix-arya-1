package com.custom.app.ui.sampleBleResult

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import kotlinx.android.synthetic.main.item_ble_result.view.*

class SampleBleAdapter(var context: Context, val scanResultList: ArrayList<BleResult>) : RecyclerView.Adapter<SampleBleAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvParameter: TextView = view.tvParameter
        var tvValue: TextView = view.tvValue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_ble_result, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvParameter.text = scanResultList[position].Parameter
        holder.tvValue.text = scanResultList[position].Value



    }

    override fun getItemCount(): Int {
        return scanResultList.size
    }

}