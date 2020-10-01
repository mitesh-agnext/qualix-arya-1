package com.custom.app.ui.scan.list.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.data.model.scanhistory.ScanData
import com.custom.app.util.Utils
import java.util.*

class ScanHistoryAdapter(val context: Context, val scanListRes: ArrayList<ScanData>, val mCallback: ListCallBack, val customerType: String) : RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder>() {
    private var isLoadingAdded = false
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvValue1: TextView = view.findViewById(R.id.tvScanScore)
        val lnScanScore: LinearLayout = view.findViewById(R.id.lnScanScore)
        val tvValue2: TextView = view.findViewById(R.id.tvValue2)
        val tvValue3: TextView = view.findViewById(R.id.tvValue3)
        val tvValue4: TextView = view.findViewById(R.id.tvValue4)
        val tvValue5: TextView = view.findViewById(R.id.tvValue5)
        val tvValue6: TextView = view.findViewById(R.id.tvValue6)
        val cdItem: LinearLayout = view.findViewById(R.id.cdItem)
        val lnScan: LinearLayout = view.findViewById(R.id.lnScan)
        val lnDevice: LinearLayout = view.findViewById(R.id.lnDevice)
        val lnEdit: LinearLayout = view.findViewById(R.id.lnEdit)
        val lnOpen: LinearLayout = view.findViewById(R.id.lnOpen)
        val lnReject: LinearLayout = view.findViewById(R.id.lnReject)
        val lnApprove: LinearLayout = view.findViewById(R.id.lnApprove)
        val tv_approve: TextView = view.findViewById(R.id.tv_approve)
        val tv_reject: TextView = view.findViewById(R.id.tv_reject)
        val view: View = view.findViewById(R.id.view)
        val lnAction: LinearLayout = view.findViewById(R.id.lnAction)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_scan_history, parent, false)
        )
    }
    override fun getItemCount(): Int {
        return scanListRes.size
    }
    fun add(mc: ScanData) {
        scanListRes.add(mc)
        notifyItemInserted(scanListRes.size - 1)
    }
    fun addAll(mcList: ArrayList<ScanData>) {
        for (mc in mcList) {
            add(mc)
        }
    }
    fun remove(city: ScanData) {
        val position: Int = scanListRes.indexOf(city)
        if (position > -1) {
            scanListRes.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }
    fun isEmpty(): Boolean {
        return itemCount == 0
    }
    fun addLoadingFooter() {
        isLoadingAdded = true
        add(ScanData())
    }
    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = scanListRes.size - 1
        val item = getItem(position)
        if (item != null) {
            scanListRes.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun getItem(position: Int): ScanData {
        return scanListRes.get(position)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.lnScanScore.visibility = View.GONE
//        if (scanListRes[position].qualityScore != null)
//            holder.tvValue1.text = scanListRes[position].qualityScore
        if (scanListRes[position].commodityName != null)
            holder.tvValue2.text = scanListRes[position].commodityName
        if (scanListRes[position].dateDone != null)
            holder.tvValue3.text = Utils.timeStampDate(scanListRes[position].dateDone!!.toLong())
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

        if (customerType=="CUSTOMER" || customerType=="OPERATOR" ) {
            holder.lnReject.visibility = View.VISIBLE
            holder.view.visibility = View.VISIBLE
            holder.lnApprove.isClickable = true
            holder.lnReject.isClickable = true
//            holder.lnAction.visibility = View.VISIBLE

            if (scanListRes[position].approval == 0 || scanListRes[position].approval == null) {
                holder.lnApprove.isClickable = true
                holder.lnReject.isClickable = true
                holder.tv_approve.setTextColor(context.resources.getColor(R.color.dark_green))
                holder.tv_reject.setTextColor(context.resources.getColor(R.color.red))

            } else if (scanListRes[position].approval == 1) {
                holder.lnApprove.isClickable = false
                holder.lnReject.isClickable = true
                holder.tv_approve.text = "Approved"
                holder.tv_approve.setTextColor(context.resources.getColor(R.color.dark_text_color))
                holder.tv_reject.setTextColor(context.resources.getColor(R.color.red))

            } else if (scanListRes[position].approval == 2) {
                holder.lnApprove.isClickable = true
                holder.lnReject.isClickable = false
                holder.tv_approve.setTextColor(context.resources.getColor(R.color.dark_green))
                holder.tv_reject.setTextColor(context.resources.getColor(R.color.dark_text_color))
                holder.tv_reject.text = "Rejected"
            }
        }
        else {
            holder.lnApprove.isClickable = false
            holder.lnReject.isClickable = false
            holder.tv_approve.visibility = View.VISIBLE
            holder.lnReject.visibility = View.GONE
            holder.view.visibility = View.GONE
//            holder.lnEdit.visibility = View.GONE

            if (scanListRes[position].approval == 0  || scanListRes[position].approval == null) {
                holder.tv_approve.text = "Pending"
                holder.tv_approve.setTextColor(context.resources.getColor(R.color.dark_text_color))
            } else if (scanListRes[position].approval == 1) {
                holder.tv_approve.text = "Approved"
                holder.tv_approve.setTextColor(context.resources.getColor(R.color.dark_green))
            } else if (scanListRes[position].approval == 2) {
                holder.tv_approve.setTextColor(context.resources.getColor(R.color.red))
                holder.tv_approve.text = "Rejected"
            }
        }

        holder.lnEdit.setOnClickListener {
            mCallback.editItem(position)
        }
        holder.lnOpen.setOnClickListener {
            mCallback.onItemClick(position)
        }
        holder.cdItem.setOnClickListener {
            mCallback.onItemClick(position)
        }
        holder.lnReject.setOnClickListener {
            mCallback.onRejectClick(position)
        }
        holder.lnApprove.setOnClickListener {
            mCallback.onApproveClick(position)
        }
    }
}