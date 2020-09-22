package com.custom.app.ui.createData.analytics.analysis.numberOfFarmers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import kotlinx.android.synthetic.main.number_of_farmers_items.view.*

class NumberOfFarmersAdapter(val context: Context, val items: ArrayList<NumberOfFarmerRes>,
                             val mCall: ShowDetailCallback) : RecyclerView.Adapter<NumberOfFarmersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var holder: ViewHolder? = null
        holder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.number_of_farmers_items, parent, false))
        return holder
    }

    override fun getItemCount(): Int {
        return items.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.farmerName.text = when {
            "" + items[position].farmer_id.toString() != "null" -> ("Farmer "+items[position].farmer_id.toString())
            else -> context.resources.getString(R.string.no_record_found)
        }
        holder.collectionCenterName.text = when {
            "" + items[position].inst_center_id != "null" -> ("Center name "+items[position].inst_center_id).toString()
            else -> context.resources.getString(R.string.no_record_found)
        }
        holder.farmerArea.text = when {
            "" + items[position].area != "null" -> (items[position].area).toString()
            else -> context.resources.getString(R.string.no_record_found)
        }
        holder.farmerCollection.text = items[position].avg_colection.toString() +" Kg"

        holder.numberOfFarmerItems.setOnClickListener {
            mCall.showDetail(position)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val farmerName = view.farmerName!!
        val collectionCenterName = view.collectionCenterName!!
        val farmerArea = view.farmerArea!!
        val farmerCollection = view.farmerCollection!!

        val numberOfFarmerItems = view.numberOfFarmerItems!!
    }

    interface ShowDetailCallback {
        fun showDetail(position: Int)
    }
}