package com.custom.app.ui.createData.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.ui.createData.analytics.analysis.quality.QualityRules
import com.google.android.material.tabs.TabLayout

class QualityRangeAdapter(val context: Context, val qualityRangeRes: ArrayList<QualityRules>) : RecyclerView.Adapter<QualityRangeAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ruleData = view.findViewById<TabLayout>(R.id.ruleDataLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_quality_range, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return qualityRangeRes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        for (i in 0 until qualityRangeRes[position].rules!!.size) {
            holder.ruleData.addTab(holder.ruleData!!.newTab().setText(qualityRangeRes[position].rules!![i].min_val.toString() + " - " + qualityRangeRes[position].rules!![i].max_val.toString()))
        }
    }
}