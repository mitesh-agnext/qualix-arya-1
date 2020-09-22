package com.custom.app.ui.createData.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.custom.app.R
import com.custom.app.ui.createData.foodType.list.FoodTypeListRes
import timber.log.Timber
import java.util.*

class FoodTypeDropdownAdapter: BaseAdapter {

    private var mContext: Context? = null
    private var foodTypeList: ArrayList<FoodTypeListRes>? = null

    constructor(context: Context, foodTypeList: ArrayList<FoodTypeListRes>) : super() {
        this.mContext = context
        this.foodTypeList = foodTypeList
    }

    override fun getCount(): Int {
        return foodTypeList!!.size
    }

    override fun getItem(i: Int): Any {
        return foodTypeList!![i]
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
            holder.tvTitleName!!.text = "${foodTypeList!![position].food_type_name}"

        } catch (e: Exception) {
            Timber.e(e)
        }

        return convertView
    }

    internal inner class ViewHolder {
        var tvTitleName: TextView? = null
    }
}