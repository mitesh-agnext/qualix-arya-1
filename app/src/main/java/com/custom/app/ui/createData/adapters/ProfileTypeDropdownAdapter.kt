package com.custom.app.ui.createData.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.custom.app.R
import com.custom.app.ui.createData.profileType.list.ProfileTypeListRes
import timber.log.Timber
import java.util.*

class ProfileTypeDropdownAdapter: BaseAdapter {

    private var mContext: Context? = null
    private var profileTypeList: ArrayList<ProfileTypeListRes>? = null

    constructor(context: Context, profileTypeList: ArrayList<ProfileTypeListRes>) : super() {
        this.mContext = context
        this.profileTypeList = profileTypeList
    }

    override fun getCount(): Int {
        return profileTypeList!!.size
    }

    override fun getItem(i: Int): Any {
        return profileTypeList!![i]
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
            holder.tvTitleName!!.text = "${profileTypeList!![position].profile_type_name}"

        } catch (e: Exception) {
            Timber.e(e)
        }

        return convertView
    }

    internal inner class ViewHolder {
        var tvTitleName: TextView? = null
    }
}