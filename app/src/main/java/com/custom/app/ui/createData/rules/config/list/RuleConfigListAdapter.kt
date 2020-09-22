package com.custom.app.ui.createData.rules.config.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_region_list.view.*

class RuleConfigListAdapter(var context: Context, val ruleConfigList: ArrayList<RuleConfigRes>,
                            val mCallback: RuleConfigListCallback) : RecyclerSwipeAdapter<RuleConfigListAdapter.ViewHolder>() {

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

        return ruleConfigList.size
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

        holder.tvRegionName.text = ruleConfigList[position].rule_config_name.toString()

        holder.inEdit_layout.setOnClickListener {
            mCallback.editRuleConfigCallback(position)
        }

        holder.InDelete_layout.setOnClickListener {
            mCallback.deleteRuleConfigCallback(position, ruleConfigList[position].rule_config_id!!.toInt())
        }
    }
}