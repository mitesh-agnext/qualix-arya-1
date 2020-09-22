package com.custom.app.ui.createData.coldstore.coldstoreList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_center_list.view.*
import kotlinx.android.synthetic.main.item_coldstore_list.view.*


class ColdstoreListAdapter(var context: Context, val centerList: ArrayList<ColdstoreRes>,
                           val mCallback: CenterListCallback) : RecyclerSwipeAdapter<ColdstoreListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvColdstoreName_list = view.tvColdstoreName_list
        var tvColdstorePlace_list = view.tvColdstorePlace_list
        var tvProfile_coldstore_list = view.tvProfile_coldstore_list
        var tvProfile_type_coldstore_list = view.tvProfile_type_coldstore_list
        var tvFood_type_coldstore_list = view.tvFood_type_coldstore_list
        var tvColdstoreNote_list = view.tvColdstoreNote_list
        var lnEdit_coldstore_list = view.lnEdit_coldstore_list
        var lnDelete_coldstore_list = view.lnDelete_coldstore_list
        var swipeLayout_coldstore_list = view.swipeLayout_coldstore_list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_coldstore_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout_coldstore_list;
    }

    override fun getItemCount(): Int {
        return centerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mItemManger.bindView(holder.itemView, position)
        holder.swipeLayout_coldstore_list.addSwipeListener(object : SwipeListener {
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
            holder.tvColdstoreName_list.text = centerList[position].inst_center_name
        } else {
            holder.tvColdstoreName_list.text = ""
        }
        if (centerList[position].site_name != null) {
            holder.tvColdstorePlace_list.text = centerList[position].site_name
        } else {
            holder.tvColdstorePlace_list.text = ""
        }
        if (centerList[position].profile_name != null) {
            holder.tvProfile_coldstore_list.text = centerList[position].profile_name!!
        } else {
            holder.tvProfile_coldstore_list.text = ""
        }
        if (centerList[position].profile_type_name != null) {
            holder.tvProfile_type_coldstore_list.text = centerList[position].profile_type_name!!
        } else {
            holder.tvProfile_type_coldstore_list.text = ""
        }
        if (centerList[position].profile_food_type_name != null) {
            holder.tvFood_type_coldstore_list.text = centerList[position].profile_food_type_name!!
        } else {
            holder.tvFood_type_coldstore_list.text = ""
        }
        if (centerList[position].notes != null) {
            holder.tvColdstoreNote_list.text = centerList[position].notes.toString()
        } else {
            holder.tvColdstoreNote_list.text = ""
        }

        holder.lnEdit_coldstore_list.setOnClickListener {
            mCallback.editCenterCallback(position)
        }

        holder.lnDelete_coldstore_list.setOnClickListener {
            mCallback.deleteCenterCallback(position, centerList[position].cold_store_id!!.toInt())
        }
    }
}