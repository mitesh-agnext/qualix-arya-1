package com.custom.app.ui.scan.list.detail

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.data.model.scanhistory.AnalysisResults

class ScanDetailAdapter(val context: Context, val resultList: ArrayList<AnalysisResults>) : RecyclerView.Adapter<ScanDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvValue = view.findViewById<TextView>(R.id.tvValue)
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
        if (resultList[position] != null) {
            if (resultList[position].analysisName != null)
                holder.tvTitle.text = resultList[position].analysisName!!.toUpperCase()
            if (resultList[position].result != null)
                if (!TextUtils.isEmpty(resultList[position].result)) {
                    holder.tvValue.text = String.format("%s %s", resultList[position].result, resultList[position].amountUnit)
                } else {
                    holder.tvValue.text = resultList[position].result
                }
        }
    }
}