package com.custom.app.ui.createData.region.site.list

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_device_list.view.*
import kotlinx.android.synthetic.main.item_site_list.view.*


class SiteListAdapter(var context: Context, val siteList: ArrayList<SiteListRes>,
                      val mCallback: SiteListCallback) : RecyclerSwipeAdapter<SiteListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvSiteName = view.tvSiteName
        var tvPlace = view.tvSitePlace
        var mainLayout = view.mainLayout
        var inEdit_layout = view.lnEdit_site
        var InDelete_layout = view.lnDelete_site
        var swipeLayout = view.swipeLayout_site
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_site_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout_site;
    }

    override fun getItemCount(): Int {

        return siteList.size
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

        holder.tvSiteName.text = siteList[position].site_name.toString()
//        holder.tvCreatedOn.text = Utils.timeStampDate(siteList[position].created_on!!)
        holder.tvPlace.text = siteList[position].country_id.toString() + ", " + siteList[position].state_id.toString() +
                ", " + siteList[position].city_id.toString()

        holder.inEdit_layout.setOnClickListener {
            mCallback.editSiteCallback(position)
        }

        holder.InDelete_layout.setOnClickListener {
            mCallback.deleteSiteCallback(position, siteList[position].site_id!!.toInt())
        }
    }

    fun moveData(position: Int, intent: Intent) {
        val gson = Gson()
        val type = object : TypeToken<SiteListRes>() {}.type
        val json = gson.toJson(siteList[position], type)
        intent.putExtra("selectObject", json)

        context.startActivity(intent)
    }
}