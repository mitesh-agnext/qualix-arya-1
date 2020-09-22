package com.custom.app.ui.device.assign.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.custom.app.R
import com.custom.app.ui.customer.list.CustomerRes
import timber.log.Timber
import java.util.*

class CustomerListAdapter : BaseAdapter {

    private var mContext: Context? = null
    private var customerList: ArrayList<CustomerRes>? = null

    constructor(context: Context, customerList: ArrayList<CustomerRes>) : super() {
        this.mContext = context
        this.customerList = customerList
    }

    override fun getCount(): Int {
        return customerList!!.size
    }

    override fun getItem(i: Int): Any {
        return customerList!![i]
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
            holder.tvTitleName!!.text = "${customerList!![position].name}"
        } catch (e: Exception) {
            Timber.e(e)
        }
        return convertView
    }

    internal inner class ViewHolder {
        var tvTitleName: TextView? = null
    }
}