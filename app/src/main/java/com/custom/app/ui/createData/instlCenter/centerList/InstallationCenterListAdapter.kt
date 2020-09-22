package com.custom.app.ui.createData.instlCenter.centerList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.ui.device.assign.InstallationCenterRes
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_center_list.view.*

class InstallationCenterListAdapter(var context: Context, val centerList: ArrayList<InstallationCenterRes>,
                                    val mCallback: CenterListCallback) : RecyclerSwipeAdapter<InstallationCenterListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvInstallationCenterName = view.tvInstallationCenterName_list
        var tvCenterTypeId = view.tvCenterTypeId_list
        var tvCenterPlace = view.tvCenterPlace_list
        var tvCenterNote = view.tvCenterNote_list
        var inEdit_layout = view.lnEdit_center_list
        var InDelete_layout = view.lnDelete_center_list
        var swipeLayout_center = view.swipeLayout_center_list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_center_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout_center_list;
    }

    override fun getItemCount(): Int {
        return centerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mItemManger.bindView(holder.itemView, position)
        holder.swipeLayout_center.addSwipeListener(object : SwipeListener {
            override fun onStartOpen(layout: SwipeLayout) {
                mItemManger.closeAllExcept(layout)
            }

            override fun onOpen(layout: SwipeLayout) {}
            override fun onStartClose(layout: SwipeLayout) {}
            override fun onClose(layout: SwipeLayout) {}
            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}
            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {}
        })
        if (centerList[position].inst_center_name != null) {
            holder.tvInstallationCenterName.text = centerList[position].inst_center_name
        } else {
            holder.tvInstallationCenterName.text = ""
        }
        if (centerList[position].commercial_location_type_desc != null) {
            holder.tvCenterTypeId.text = centerList[position].commercial_location_type_desc
        } else {
            holder.tvCenterTypeId.text = ""
        }
        if (centerList[position].site_name != null) {
            holder.tvCenterPlace.text = centerList[position].site_name
        } else {
            holder.tvCenterPlace.text = ""
        }
        if (centerList[position].notes != null) {
            holder.tvCenterNote.text = centerList[position].notes.toString()
        } else {
            holder.tvCenterNote.text = ""
        }
        holder.inEdit_layout.setOnClickListener {
            mCallback.editCenterCallback(position)
        }
        holder.InDelete_layout.setOnClickListener {
            mCallback.deleteCenterCallback(position, centerList[position].installation_center_id!!.toInt())
        }
    }
}