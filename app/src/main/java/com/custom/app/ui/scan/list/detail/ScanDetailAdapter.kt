package com.custom.app.ui.scan.list.detail

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.data.model.scanhistory.AnalysisResults
import com.custom.app.data.model.scanhistory.ScanData

class ScanDetailAdapter(val context: Context, val resultList: ArrayList<AnalysisResults>, val testObject: ScanData) : RecyclerView.Adapter<ScanDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvWeight = view.findViewById<TextView>(R.id.tvWeight)
        val tvCount = view.findViewById<TextView>(R.id.tvCount)
        val tvRange = view.findViewById<TextView>(R.id.tvRange)
        val lnRow = view.findViewById<LinearLayout>(R.id.lnRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_scan_detail_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rangeList = java.util.ArrayList<Double>()
        rangeList.add(0.0)
        rangeList.add(70.0)
        rangeList.add(8.0)
        rangeList.add(2.0)
        rangeList.add(4.0)
        rangeList.add(2.0)
        rangeList.add(4.0)

        if (resultList.get(position) != null) {
            if (resultList.get(position).analysisName != null)
                holder.tvTitle.text = resultList.get(position).analysisName!!.toUpperCase()

            holder.tvRange.visibility = View.GONE

            if (testObject.commodityName.equals("Peanut")) {
                if (resultList.get(position).totalAmount != null)
                    holder.tvCount.text = resultList.get(position).totalAmount.toString()
                if (resultList.get(position).byDensityResult != null)
                    holder.tvWeight.text = resultList.get(position).byDensityResult.toString()

                holder.tvRange.visibility = View.VISIBLE
                holder.tvRange.text = rangeList.get(position).toString()
                if (resultList.get(position).byDensityResult != null) {
                    if (resultList.get(position).byDensityResult!!.toDouble() > rangeList[1]) {
                        holder.tvCount.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvTitle.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvWeight.setTextColor(Color.parseColor("#4CAF50"))
                    } else if (rangeList[2] > resultList.get(position).byDensityResult!!.toDouble()) {
                        holder.tvCount.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvTitle.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvWeight.setTextColor(Color.parseColor("#4CAF50"))
                    } else if (rangeList[3] > resultList.get(position).byDensityResult!!.toDouble()) {
                        holder.tvCount.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvTitle.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvWeight.setTextColor(Color.parseColor("#4CAF50"))
                    } else if (rangeList[4] > resultList.get(position).byDensityResult!!.toDouble()) {
                        holder.tvCount.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvTitle.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvWeight.setTextColor(Color.parseColor("#4CAF50"))
                    } else if (rangeList[5] > resultList.get(position).byDensityResult!!.toDouble()) {
                        holder.tvCount.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvTitle.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvWeight.setTextColor(Color.parseColor("#4CAF50"))
                    } else if (rangeList[6] > resultList.get(position).byDensityResult!!.toDouble()) {
                        holder.tvCount.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvTitle.setTextColor(Color.parseColor("#4CAF50"))
                        holder.tvWeight.setTextColor(Color.parseColor("#4CAF50"))
                    } else {
                        holder.tvCount.setTextColor(Color.parseColor("#ff0000"))
                        holder.tvTitle.setTextColor(Color.parseColor("#ff0000"))
                        holder.tvWeight.setTextColor(Color.parseColor("#ff0000"))
                    }
                }
            } else {
                holder.tvRange.visibility = View.GONE
                holder.tvCount.visibility = View.GONE
                if (resultList[position].totalAmount != null)
                    if (!TextUtils.isEmpty(resultList[position].totalAmount)) {
                        holder.tvWeight.text = ""+resultList[position].totalAmount
                    } else {
                        holder.tvWeight.text = resultList[position].totalAmount
                    }
            }
        }
    }
}