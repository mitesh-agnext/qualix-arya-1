package com.custom.app.ui.createData.flcScan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.custom.app.R
import com.custom.app.ui.createData.flcScan.DevisionList

class FlcDevisionAdapter: BaseAdapter {

    private var mContext: Context? = null
    private var devisionList: ArrayList<DevisionList>? = null

    constructor(context: Context, devisionList: ArrayList<DevisionList>) : super() {
        this.mContext = context
        this.devisionList = devisionList
    }

    override fun getCount(): Int {
        return devisionList!!.size
    }

    override fun getItem(i: Int): Any {
        return devisionList!![i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        var holder: ViewHolder? = null

        if (convertView == null) {
            // If convertView is null then inflate the appropriate layout file
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_spinner_plain, null)

            holder = ViewHolder()

            holder.tvTitleName = convertView!!.findViewById(R.id.tv_title)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        try {
            holder.tvTitleName!!.text = "${devisionList!![position].division_name}"

        } catch (e: Exception) {
            e.printStackTrace()

        }
        return convertView
    }


    internal inner class ViewHolder {
        var tvTitleName: TextView? = null
    }
}