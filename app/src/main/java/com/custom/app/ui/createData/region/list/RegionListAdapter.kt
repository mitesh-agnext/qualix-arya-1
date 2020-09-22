package com.custom.app.ui.createData.region.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.ui.createData.region.site.create.RegionRes
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_region_list.view.*

class RegionListAdapter(var context: Context, val regionList: ArrayList<RegionRes>,
                        val mCallback: RegionListCallback) : RecyclerSwipeAdapter<RegionListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvRegionName = view.tvRegionName
//        var tvRegion_createdOn = view.tvRegionCreatedOn
        var mainLayout = view.mainLayout_region
        var inEdit_layout = view.lnEdit_region
        var InDelete_layout = view.lnDelete_region
        var swipeLayout = view.swipeLayout_region
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_region_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout_region;
    }

    override fun getItemCount(): Int {

        return regionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mItemManger.bindView(holder.itemView, position)
        holder.swipeLayout.addSwipeListener(object : SwipeListener {
            override fun onStartOpen(layout: SwipeLayout) {
                mItemManger.closeAllExcept(layout)
            }

            override fun onOpen(layout: SwipeLayout) {}
            override fun onStartClose(layout: SwipeLayout) {}
            override fun onClose(layout: SwipeLayout) {}
            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}
            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {}
        })

        holder.swipeLayout.close()
        holder.tvRegionName.text = regionList[position].region_name.toString()

        holder.inEdit_layout.setOnClickListener{
            mCallback.editRegionCallback(position, holder.swipeLayout)
    }

    holder.InDelete_layout.setOnClickListener{
        mCallback.deleteRegionCallback(position, regionList[position].region_id!!.toInt(), holder.swipeLayout)
    }
}
}