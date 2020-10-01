package com.custom.app.ui.qualityAnalysis

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.ui.scan.list.history.ListCallBack
import com.custom.app.util.Utils
import java.util.ArrayList

class QualityAnalysisAdapter(val context: Context, val scanListRes: ArrayList<ScanData>,
                             val mCallback: ListCallBack) : RecyclerView.Adapter<QualityAnalysisAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvValue1: TextView = view.findViewById(R.id.tvValue1)
        val tvValue2: TextView = view.findViewById(R.id.tvValue2)
        val tvValue3: TextView = view.findViewById(R.id.tvValue3)
        val tvValue4: TextView = view.findViewById(R.id.tvValue4)
        val tvValue5: TextView = view.findViewById(R.id.tvValue5)
        val tvValue6: TextView = view.findViewById(R.id.tvValue6)
        val cdItem: LinearLayout = view.findViewById(R.id.cdItem)
        val lnScan: LinearLayout = view.findViewById(R.id.lnScan)
        val lnDevice: LinearLayout = view.findViewById(R.id.lnDevice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.farmer_scan_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return scanListRes.size
    }

    override fun onBindViewHolder(holder: QualityAnalysisAdapter.ViewHolder, position: Int) {
        if (scanListRes[position].qualityScore != null)
            holder.tvValue1.text = scanListRes[position].qualityScore
        if (scanListRes[position].commodityName != null)
            holder.tvValue2.text = scanListRes[position].commodityName

        if (scanListRes[position].dateDone != null)
            holder.tvValue3.text = Utils.getTimeFromEpoch(scanListRes[position].dateDone!!.toLong())
        holder.tvValue4.text = scanListRes[position].batchId
        if (scanListRes[position].scanId != null) {
            holder.lnScan.visibility = View.VISIBLE
            holder.tvValue5.text = scanListRes[position].scanId
        } else
            holder.lnScan.visibility = View.GONE
        if (scanListRes[position].deviceName != null) {
            holder.lnDevice.visibility = View.VISIBLE
            holder.tvValue6.text = scanListRes[position].deviceName
        } else
            holder.lnDevice.visibility = View.GONE
    }
}