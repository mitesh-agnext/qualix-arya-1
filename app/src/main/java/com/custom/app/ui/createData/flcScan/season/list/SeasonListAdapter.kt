package com.custom.app.ui.createData.flcScan.season.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.util.Utils
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_season_list.view.*

class SeasonListAdapter(var context: Context, val seasonList: ArrayList<SeasonRes>,
                        val mCallback: SeasonListCallback) : RecyclerSwipeAdapter<SeasonListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvSeasonName = view.tvSeason_name
        var tvSeasonEquation = view.tvSeason_equation
        var tvCommodityName_list = view.tvCommodityName_list
        var tvDateFrom = view.tvDate_from
        var tvDateTo = view.tvDate_to_list

        var mainLayout = view.mainLayout_season_list
        var inEdit_layout = view.lnEdit_season_list
        var InDelete_layout = view.lnDelete_season_list
        var swipeLayout = view.swipeLayout_season_list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_season_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout_season_list;
    }

    override fun getItemCount(): Int {

        return seasonList.size
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

        holder.tvSeasonName.text = seasonList[position].season_name.toString()
        holder.tvSeasonEquation.text = seasonList[position].season_equation.toString()
        holder.tvDateFrom.text = Utils.timeStampDate(seasonList[position].from_date!!)
        holder.tvDateTo.text = Utils.timeStampDate(seasonList[position].to_date!!)
        holder.tvCommodityName_list.text = seasonList[position].commodity_name.toString()

        holder.inEdit_layout.setOnClickListener {
            mCallback.editSeasonCallback(position)
        }

        holder.InDelete_layout.setOnClickListener {
            mCallback.deleteSeasonCallback(position, seasonList[position].season_id!!.toInt())
        }
    }
}