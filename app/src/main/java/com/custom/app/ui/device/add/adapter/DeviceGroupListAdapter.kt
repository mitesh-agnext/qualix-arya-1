package com.custom.app.ui.device.add.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.custom.app.R
import com.custom.app.ui.device.add.DeviceGroupRes
import timber.log.Timber
import java.util.*

class DeviceGroupListAdapter: BaseAdapter {

    private var mContext: Context? = null
    private var deviceGroupList: ArrayList<DeviceGroupRes>? = null

    constructor(context: Context, tutorialList: ArrayList<DeviceGroupRes>) : super() {
        this.mContext = context
        this.deviceGroupList = tutorialList
    }

    override fun getCount(): Int {
        return deviceGroupList!!.size
    }

    override fun getItem(i: Int): Any {
        return deviceGroupList!![i]
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
            holder.tvTitleName!!.text = "${deviceGroupList!![position].device_group_desc}"
        } catch (e: Exception) {
            Timber.e(e)
        }

        return convertView
    }

    internal inner class ViewHolder {
        var tvTitleName: TextView? = null
    }
}