package com.custom.app.ui.section.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.custom.app.data.model.section.SectionRes
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_farm_list.view.*

class SectionListAdapter(val context: Context, val farmList: ArrayList<SectionRes>,
                         val mCallback: FarmCallback) : RecyclerSwipeAdapter<SectionListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFarmName: TextView = view.tvFarmName
        val tvArea: TextView = view.tvArea
        val tvFarmCode: TextView = view.tvFarmCode
        val tvGarden: TextView = view.tvGarden
        val tvDivision: TextView = view.tvDivision

        val swipeLayout: SwipeLayout = view.swipeLayout
        val mainItem: LinearLayout = view.mainItem
        val lnEdit: LinearLayout = view.lnEdit
        val lnDelete: LinearLayout = view.lnDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_farm_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout;
    }

    override fun getItemCount(): Int {
        return farmList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvFarmName.text = farmList[pos].name
        holder.tvDivision.text = farmList[pos].division_name
        holder.tvGarden.text = farmList[pos].garden_name
        holder.tvArea.text = farmList[pos].total_area
        holder.tvFarmCode.text = "SEC ${farmList[pos].section_id}"
        mItemManger.bindView(holder.itemView, pos)

        holder.lnDelete.setOnClickListener {
            holder.swipeLayout.close()
            mCallback.deleteCustomerCallback(pos)
        }
        holder.lnEdit.setOnClickListener {
            holder.swipeLayout.close()
            mCallback.editCustomerCallback(pos)
        }
        holder.mainItem.setOnClickListener {
            holder.swipeLayout.close()
            mCallback.itemClick(pos)
        }
        holder.swipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener {
            override fun onStartOpen(layout: SwipeLayout) {
                mItemManger.closeAllExcept(layout)
            }

            override fun onOpen(layout: SwipeLayout) {}
            override fun onStartClose(layout: SwipeLayout) {}
            override fun onClose(layout: SwipeLayout) {}
            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}
            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {}
        })
    }
}

interface FarmCallback {

    fun editCustomerCallback(pos: Int)
    fun deleteCustomerCallback(pos: Int)
    fun itemClick(pos: Int)

}