package com.custom.app.ui.createData.deviationProfile.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.app.R
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_deviations_list.view.*

class DeviationListAdapter(var context: Context, val deviationList: ArrayList<DeviationListRes>,
                           val mCallback: DeviationListCallback) : RecyclerSwipeAdapter<DeviationListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvProfileName = view.tvProfileName_deviation
        var tvEquation = view.tvEquation_deviation
        var mainLayout = view.mainLayout_deviation
        var inEdit_layout = view.lnEdit_deviation
        var InDelete_layout = view.lnDelete_deviation
        var swipeLayout = view.swipeLayout_deviation
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_device_list, parent, false)
        )
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout_region;
    }

    override fun getItemCount(): Int {
        return deviationList.size
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

//        holder.tvFoodTypeName.text = deviationList[position].food_type_name.toString()

        holder.inEdit_layout.setOnClickListener {
            //            val intent = Intent(context, FoodTypeUpdate::class.java)
//            moveData(position, intent)
        }

//        holder.InDelete_layout.setOnClickListener {
//            mCallback.deleteFoodTypeCallback(position, deviationList[position].food_type_id!!.toInt())
//        }

//        holder.titleFoodTypeName.text = "Food Type name"

    }

}