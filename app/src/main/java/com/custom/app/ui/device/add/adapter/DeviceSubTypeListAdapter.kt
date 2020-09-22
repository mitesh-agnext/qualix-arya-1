package com.custom.app.ui.device.add.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.custom.app.R
import com.custom.app.ui.device.add.DeviceSubTypeRes
import timber.log.Timber
import java.util.*

class DeviceSubTypeListAdapter: BaseAdapter {

    private var mContext: Context? = null
    private var deviceSubTypeList: ArrayList<DeviceSubTypeRes>? = null

    constructor(context: Context, tutorialList: ArrayList<DeviceSubTypeRes>) : super() {
        this.mContext = context
        this.deviceSubTypeList = tutorialList
    }

    override fun getCount(): Int {
        return deviceSubTypeList!!.size
    }

    override fun getItem(i: Int): Any {
        return deviceSubTypeList!![i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        var holder: ViewHolder? = null

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_spinner_plain, null)

            holder = ViewHolder()

            holder.tvTitleName = convertView!!.findViewById(R.id.tv_title)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        try {
            holder.tvTitleName!!.text = "${deviceSubTypeList!![position].device_sub_type_desc}"

        } catch (e: Exception) {
            Timber.e(e)
        }

        return convertView
    }

    internal inner class ViewHolder {
        var tvTitleName: TextView? = null
    }
}